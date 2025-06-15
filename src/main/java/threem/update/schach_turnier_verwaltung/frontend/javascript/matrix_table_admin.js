let players = [];
let matchResults = {};
let tournamentId = '';
let tournamentName = '';

function getUrlParameter(name) {
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    var results = regex.exec(location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
}

function goBack() {
    window.history.back();
}

function resetMatrix() {
    if (confirm('Are you sure you want to reset all match results to N/A?')) {
        players.forEach(player1 => {
            players.forEach(player2 => {
                if (player1.id !== player2.id) {
                    matchResults[`${player1.id}-${player2.id}`] = 'N/A';
                }
            });
        });
        updateMatrix();
    }
}

function fetchPlayersFromTournament(tournamentId) {
    fetch(`http://localhost:8080/persons/getPersonByTournament/${tournamentId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Fetched players:', data);
            players = data;

            const playerIdToName = {};
            players.forEach(player => {
                playerIdToName[player.id] = player.username;
            });

            buildMatrix(players);

            fetchMatchResults(tournamentId, playerIdToName);
        })
        .catch(error => {
            console.error('Error fetching players:', error);
            alert('Failed to load players. Please try again later.');
        });
}

function buildMatrix(players) {
    const table = document.getElementById('match-matrix');

    table.innerHTML = '';

    const headerRow = document.createElement('tr');
    const cornerCell = document.createElement('th');
    cornerCell.textContent = 'Players';
    headerRow.appendChild(cornerCell);

    players.forEach(player => {
        const th = document.createElement('th');
        th.textContent = player.username + ' (' + player.id + ')';
        headerRow.appendChild(th);
    });

    table.appendChild(headerRow);

    players.forEach((rowPlayer, rowIndex) => {
        const row = document.createElement('tr');

        const nameCell = document.createElement('td');
        nameCell.textContent = rowPlayer.username + ' (' + rowPlayer.id + ')';
        nameCell.className = 'player-name';
        row.appendChild(nameCell);

        players.forEach((colPlayer, colIndex) => {
            const cell = document.createElement('td');

            if (rowIndex === colIndex) {
                cell.className = 'match-cell disabled';
                cell.textContent = 'X';
            } else {
                cell.className = 'match-cell';
                cell.dataset.row = rowPlayer.id;
                cell.dataset.col = colPlayer.id;
                cell.dataset.rowName = rowPlayer.username;
                cell.dataset.colName = colPlayer.username;

                cell.textContent = '← N/A';
                cell.classList.add('not-played');

                cell.addEventListener('click', function() {
                    showResultSelector(this);
                });
            }

            row.appendChild(cell);
        });

        table.appendChild(row);
    });
}

function showResultSelector(cell) {
    const existingSelectors = document.querySelectorAll('.result-selector');
    existingSelectors.forEach(selector => selector.remove());

    const selector = document.createElement('div');
    selector.className = 'result-selector';

    const options = [
        { text: 'Win', value: 'W', class: 'win' },
        { text: 'Loss', value: 'L', class: 'loss' },
        { text: 'Draw', value: 'D', class: 'draw' },
        { text: 'N/A', value: 'N/A', class: 'not-played' }
    ];

    options.forEach(option => {
        const optionElement = document.createElement('div');
        optionElement.className = 'result-option';
        optionElement.textContent = option.text;

        optionElement.addEventListener('click', function() {
            setMatchResult(cell, option.value, option.class);
            selector.remove();
        });

        selector.appendChild(optionElement);
    });

    cell.appendChild(selector);
    selector.style.display = 'block';

    document.addEventListener('click', function closeSelector(e) {
        if (!selector.contains(e.target) && e.target !== cell) {
            selector.remove();
            document.removeEventListener('click', closeSelector);
        }
    });
}

function setMatchResult(cell, result, resultClass) {
    const rowPlayerId = cell.dataset.row;
    const colPlayerId = cell.dataset.col;
    const rowPlayerName = cell.dataset.rowName;
    const colPlayerName = cell.dataset.colName;

    matchResults[`${rowPlayerId}-${colPlayerId}`] = result;

    let inverseResult = '';
    let inverseClass = '';

    if (result === 'W') {
        inverseResult = 'L';
        inverseClass = 'loss';
    } else if (result === 'L') {
        inverseResult = 'W';
        inverseClass = 'win';
    } else if (result === 'D') {
        inverseResult = 'D';
        inverseClass = 'draw';
    } else if (result === 'N/A') {
        inverseResult = 'N/A';
        inverseClass = 'not-played';
    }

    if (inverseResult) {
        matchResults[`${colPlayerId}-${rowPlayerId}`] = inverseResult;

        const inverseCell = document.querySelector(`.match-cell[data-row="${colPlayerId}"][data-col="${rowPlayerId}"]`);
        if (inverseCell) {
            updateCellDisplay(inverseCell, inverseResult, inverseClass);
        }
    }

    updateCellDisplay(cell, result, resultClass);
}

function updateCellDisplay(cell, result, resultClass) {
    cell.textContent = '';
    cell.classList.remove('win', 'loss', 'draw', 'not-played');

    cell.textContent = `← ${result}`;
    cell.classList.add(resultClass);
}

function updateMatrix() {
    const cells = document.querySelectorAll('.match-cell:not(.disabled)');
    cells.forEach(cell => {
        updateCellDisplay(cell, 'N/A', 'not-played');
    });

    for (const [key, result] of Object.entries(matchResults)) {
        const [rowPlayerId, colPlayerId] = key.split('-');
        const cell = document.querySelector(`.match-cell[data-row="${rowPlayerId}"][data-col="${colPlayerId}"]`);

        if (cell) {
            let resultClass = '';
            if (result === 'W') resultClass = 'win';
            else if (result === 'L') resultClass = 'loss';
            else if (result === 'D') resultClass = 'draw';
            else if (result === 'N/A') resultClass = 'not-played';

            updateCellDisplay(cell, result, resultClass);
        }
    }
}

function fetchMatchResults(tournamentId, playerIdToName) {
    matchResults = {};
    players.forEach(player1 => {
        players.forEach(player2 => {
            if (player1.id !== player2.id) {
                matchResults[`${player1.id}-${player2.id}`] = 'N/A';
            }
        });
    });

    fetch(`http://localhost:8080/matches/getmatches/${tournamentId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            for (const [key, result] of Object.entries(data)) {
                const [player1Id, player2Id] = key.split('-');

                matchResults[key] = result;

                let inverseResult = '';
                if (result === 'W') {
                    inverseResult = 'L';
                } else if (result === 'L') {
                    inverseResult = 'W';
                } else if (result === 'D') {
                    inverseResult = 'D';
                } else if (result === 'N/A') {
                    inverseResult = 'N/A';
                }

                if (inverseResult) {
                    matchResults[`${player2Id}-${player1Id}`] = inverseResult;
                }
            }

            updateMatrix();
        })
        .catch(error => {
            console.error('Error fetching match results:', error);
            console.log('Failed to load match results. The matrix will be empty.');
            updateMatrix();
        });
}

function saveMatches() {
    fetch(`http://localhost:8080/matches/addmatches/${tournamentId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(matchResults)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            alert('Match results saved successfully!');
            console.log('Save response:', data);
        })
        .catch(error => {
            console.error('Error saving match results:', error);
            alert('Failed to save match results. Please try again later.');
        });
}

window.onload = function() {
    tournamentId = getUrlParameter('tournamentId');
    tournamentName = getUrlParameter('tournamentName');

    document.getElementById('tournament-title').textContent = 'Tournament: ' + tournamentName;

    if (tournamentId) {
        fetchPlayersFromTournament(tournamentId);
    } else {
        alert('No tournament ID provided. Please go back and select a tournament.');
    }
};