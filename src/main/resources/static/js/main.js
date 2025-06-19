document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
    const dropArea = document.getElementById('drop-area');
    const fileInput = document.getElementById('fileInput');
    const fileList = document.getElementById('file-list');
    const analyzeBtn = document.getElementById('analyze-btn');
    const resultsContainer = document.getElementById('results-container');
    const loadingOverlay = document.getElementById('loading-overlay');

    // Selected files array
    let selectedFiles = [];

    // Add event listeners for drag and drop
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dropArea.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    ['dragenter', 'dragover'].forEach(eventName => {
        dropArea.addEventListener(eventName, highlight, false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dropArea.addEventListener(eventName, unhighlight, false);
    });

    function highlight() {
        dropArea.classList.add('highlight');
    }

    function unhighlight() {
        dropArea.classList.remove('highlight');
    }

    // Handle dropped files
    dropArea.addEventListener('drop', handleDrop, false);

    function handleDrop(e) {
        const dt = e.dataTransfer;
        const files = dt.files;
        handleFiles(files);
    }

    // Handle files from file input
    fileInput.addEventListener('change', function() {
        handleFiles(this.files);
    });

    // Process the selected files
    function handleFiles(files) {
        if (files.length === 0) return;

        // Convert FileList to array and add to selectedFiles
        Array.from(files).forEach(file => {
            // Check if file is already in the list
            if (!selectedFiles.some(f => f.name === file.name && f.size === file.size)) {
                selectedFiles.push(file);
            }
        });

        updateFileList();
        updateAnalyzeButton();
    }

    // Update the file list UI
    function updateFileList() {
        fileList.innerHTML = '';

        selectedFiles.forEach((file, index) => {
            const fileItem = document.createElement('li');
            fileItem.className = 'file-item';

            const fileInfo = document.createElement('div');
            fileInfo.className = 'file-info';

            const fileIcon = document.createElement('i');
            fileIcon.className = 'fas fa-file file-icon';

            const fileName = document.createElement('span');
            fileName.className = 'file-name';
            fileName.textContent = file.name;

            const fileSize = document.createElement('span');
            fileSize.className = 'file-size';
            fileSize.textContent = `(${formatFileSize(file.size)})`;

            fileInfo.appendChild(fileIcon);
            fileInfo.appendChild(fileName);
            fileInfo.appendChild(fileSize);

            const removeBtn = document.createElement('i');
            removeBtn.className = 'fas fa-times remove-file';
            removeBtn.addEventListener('click', () => {
                selectedFiles.splice(index, 1);
                updateFileList();
                updateAnalyzeButton();
            });

            fileItem.appendChild(fileInfo);
            fileItem.appendChild(removeBtn);
            fileList.appendChild(fileItem);
        });
    }

    // Format file size to human-readable format
    function formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';

        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));

        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    // Update analyze button state
    function updateAnalyzeButton() {
        analyzeBtn.disabled = selectedFiles.length === 0;
    }

    // Handle analyze button click
    analyzeBtn.addEventListener('click', analyzeFiles);

    function analyzeFiles() {
        if (selectedFiles.length === 0) return;

        // Show loading overlay
        loadingOverlay.style.display = 'flex';

        // Create FormData and append files
        const formData = new FormData();
        selectedFiles.forEach(file => {
            formData.append('files', file);
        });

        // Send files to server for analysis
        fetch('/api/analyzer/analyze', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Server error: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            displayResults(data);
            loadingOverlay.style.display = 'none';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while analyzing files. Please try again.');
            loadingOverlay.style.display = 'none';
        });
    }

    // Display analysis results
    function displayResults(data) {
        // Clear previous results
        resultsContainer.innerHTML = '';

        if (data.length === 0) {
            resultsContainer.innerHTML = '<p class="no-results">No results to display.</p>';
            return;
        }

        // Process each result
        data.forEach(result => {
            const resultCard = document.createElement('div');
            resultCard.className = 'result-card';

            const resultHeader = document.createElement('div');
            resultHeader.className = 'result-header';

            const fileDetails = document.createElement('div');
            fileDetails.className = 'file-details';

            const fileName = document.createElement('h3');
            fileName.textContent = result.fileName;

            const fileType = document.createElement('span');
            fileType.className = 'file-type';
            fileType.textContent = result.fileType;

            fileDetails.appendChild(fileName);
            fileDetails.appendChild(fileType);

            resultHeader.appendChild(fileDetails);

            // Create algorithm results
            const algorithmResults = document.createElement('div');
            algorithmResults.className = 'algorithm-results';

            const algorithmTitle = document.createElement('h4');
            algorithmTitle.textContent = 'Algorithm Performance';

            const table = document.createElement('table');
            table.className = 'algorithm-table';

            const thead = document.createElement('thead');
            const headerRow = document.createElement('tr');

            const algorithmHeader = document.createElement('th');
            algorithmHeader.textContent = 'Algorithm';

            const timeHeader = document.createElement('th');
            timeHeader.textContent = 'Time (ns)'; // Changed from ms to ns

            headerRow.appendChild(algorithmHeader);
            headerRow.appendChild(timeHeader);
            thead.appendChild(headerRow);

            const tbody = document.createElement('tbody');

            // Find fastest algorithm
            const timings = result.algorithmTimings;
            const fastestAlgorithm = Object.keys(timings).reduce((a, b) =>
                timings[a] < timings[b] ? a : b);

            // Add rows for each algorithm
            Object.entries(timings).forEach(([algorithm, time]) => {
                const row = document.createElement('tr');

                const algorithmCell = document.createElement('td');
                algorithmCell.textContent = formatAlgorithmName(algorithm);

                const timeCell = document.createElement('td');
                timeCell.textContent = formatNanoseconds(time); // Use the new formatting function

                if (algorithm === fastestAlgorithm) {
                    timeCell.classList.add('fastest');
                    algorithmCell.classList.add('fastest');
                }

                row.appendChild(algorithmCell);
                row.appendChild(timeCell);
                tbody.appendChild(row);
            });

            table.appendChild(thead);
            table.appendChild(tbody);

            algorithmResults.appendChild(algorithmTitle);
            algorithmResults.appendChild(table);

            // Assemble the result card
            resultCard.appendChild(resultHeader);
            resultCard.appendChild(algorithmResults);

            resultsContainer.appendChild(resultCard);
        });

        // Scroll to results
        document.getElementById('results-section').scrollIntoView({ behavior: 'smooth' });
    }

    // Format algorithm name for display
    function formatAlgorithmName(name) {
        switch(name) {
            case 'BruteForce':
                return 'Brute Force';
            case 'KMP':
                return 'Knuth-Morris-Pratt';
            case 'RabinKarp':
                return 'Rabin-Karp';
            case 'SlidingWindow':
                return 'Sliding Window';
            default:
                return name;
        }
    }

    // Add a new function to format nanoseconds with commas for readability
    function formatNanoseconds(nanos) {
        return nanos.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
});