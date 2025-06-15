window.onload = function() {
    const userData = sessionStorage.getItem('userData');

    if (!userData) {
        // noch nicht angemeldet zurück zu login
        window.location.href = 'login.html';
        return;
    }

    try {
        const dataArray = JSON.parse(userData);

        console.log("Data from sessionStorage:", dataArray);

        if (!Array.isArray(dataArray) || dataArray.length === 0) {
            console.error("Invalid data format in sessionStorage");
            return;
        }

        const user = dataArray[0];

        document.getElementById('welcome-username').textContent = user.username;
        document.getElementById('person-id').textContent = user.id || user.personId;
        document.getElementById('person-username').textContent = user.username;
        document.getElementById('person-wins').textContent = user.wins || 0;
        document.getElementById('person-losses').textContent = user.losses || 0;
        document.getElementById('person-draws').textContent = user.draws || 0;

        const tournaments = dataArray.slice(1);

        refreshTournaments();
    } catch (error) {
        console.error("Error processing user data:", error);
    }
};

function refreshTournaments() {
    const userData = sessionStorage.getItem('userData');
    if (!userData) return;

    try {
        const dataArray = JSON.parse(userData);
        const user = dataArray[0];

        // Fetch updated data
        fetch(`http://localhost:8080/persons/getperson/${user.username}/${user.password}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Server responded with status: ${response.status}`);
                }
                return response.text();
            })
            .then(text => {
                if(text === "Benutzername oder Passwort falsch") {
                    console.error("Authentication error during refresh");
                    return null;
                } else {
                    return JSON.parse(text);
                }
            })
            .then(data => {
                if (data === null) return;

                sessionStorage.setItem('userData', JSON.stringify(data));

                const tournaments = data.slice(1);
                displayTournaments(tournaments);
            })
            .catch(error => {
                console.error("Error refreshing data:", error);
            });
    } catch (error) {
        console.error("Error processing user data during refresh:", error);
    }
}

function displayTournaments(tournaments) {
    const tournamentList = document.getElementById('tournament-list');
    const noTournamentsMessage = document.getElementById('no-tournaments');

    if (!tournaments || tournaments.length === 0) {
        noTournamentsMessage.style.display = 'block';
        return;
    }

    noTournamentsMessage.style.display = 'none';

    tournamentList.removeChild(noTournamentsMessage);

    tournamentList.innerHTML = '';

    tournamentList.appendChild(noTournamentsMessage);

    tournaments.forEach(tournament => {
        const tournamentItem = document.createElement('div');
        tournamentItem.className = 'tournament-item';

        const tournamentName = document.createElement('div');
        tournamentName.className = 'tournament-name';
        tournamentName.textContent = tournament.name;

        const tournamentDates = document.createElement('div');
        tournamentDates.className = 'tournament-dates';

        const startDate = new Date(tournament.start);
        const endDate = new Date(tournament.end);
        const dateOptions = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };

        tournamentDates.textContent = `From: ${startDate.toLocaleDateString(undefined, dateOptions)} - To: ${endDate.toLocaleDateString(undefined, dateOptions)}`;

        const matrixButton = document.createElement('button');
        matrixButton.style.backgroundColor = '#2196F3';
        matrixButton.style.color = 'white';
        matrixButton.style.padding = '5px 10px';
        matrixButton.style.border = 'none';
        matrixButton.style.borderRadius = '4px';
        matrixButton.style.cursor = 'pointer';
        matrixButton.style.marginTop = '10px';
        matrixButton.textContent = 'Ranking';
        matrixButton.onclick = function() {
            window.location.href = `matrix_table.html?tournamentId=${tournament.tournamentId || tournament.id}&tournamentName=${encodeURIComponent(tournament.name)}`;
        };

        tournamentItem.appendChild(tournamentName);
        tournamentItem.appendChild(tournamentDates);
        tournamentItem.appendChild(matrixButton);

        tournamentList.appendChild(tournamentItem);
    });
}

function addToTournament() {
    const tournamentId = document.getElementById('tournament-id').value;
    const messageDiv = document.getElementById('tournament-message');

    if (!tournamentId || isNaN(tournamentId) || tournamentId <= 0) {
        messageDiv.textContent = 'Please enter a valid tournament ID';
        messageDiv.style.display = 'block';
        messageDiv.style.backgroundColor = '#ffcccc';
        return;
    }

    const userData = sessionStorage.getItem('userData');
    if (!userData) {
        window.location.href = 'login.html';
        return;
    }

    const dataArray = JSON.parse(userData);
    const user = dataArray[0];
    const personId = user.id || user.personId;

    fetch(`http://localhost:8080/persons/addpersontotournament/${personId}/${tournamentId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server responded with status: ${response.status}`);
            }
            return response.text();
        })
        .then(result => {
            if (result === "Tournament existiert nicht") {
                messageDiv.textContent = 'This tournament does not exist';
                messageDiv.style.backgroundColor = '#ffcccc';
            } else if (result === "Spieler ist bereits für dieses Tournament registriert") {
                messageDiv.textContent = 'Error: You are already registered for this tournament';
                messageDiv.style.backgroundColor = '#ffcccc';
            } else if (result === "SQL Fehler") {
                messageDiv.textContent = 'A database error occurred';
                messageDiv.style.backgroundColor = '#ffcccc';
            } else {
                messageDiv.textContent = 'Successfully joined the tournament';
                messageDiv.style.backgroundColor = '#ccffcc';

                document.getElementById('tournament-id').value = '';

                refreshTournaments();
            }
            messageDiv.style.display = 'block';
        })
        .catch(error => {
            console.error('Error:', error);
            messageDiv.textContent = 'An error occurred. Please try again.';
            messageDiv.style.display = 'block';
            messageDiv.style.backgroundColor = '#ffcccc';
        });
}

function logout() {
    sessionStorage.removeItem('userData');

    window.location.href = 'login.html';
}