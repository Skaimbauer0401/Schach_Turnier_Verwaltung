function setDefaultDateTimeValues() {
    const now = new Date();

    const dateString = now.toISOString().split('T')[0];

    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const timeString = `${hours}:${minutes}`;

    document.getElementById('start-date').value = dateString;
    document.getElementById('start-time').value = timeString;

    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const tomorrowDateString = tomorrow.toISOString().split('T')[0];

    document.getElementById('end-date').value = tomorrowDateString;
    document.getElementById('end-time').value = timeString;
}

window.onload = function() {


    const userData = sessionStorage.getItem('userData');

    if (!userData) {
        window.location.href = 'login.html';
        return;
    }

    setDefaultDateTimeValues();

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
        document.getElementById('person-admin').textContent = (user.admin !== undefined) ? (user.admin ? 'Yes' : 'No') : 'Unknown';

        const tournaments = dataArray.slice(1);


        refreshUserData();
    } catch (error) {
        console.error("Error processing user data:", error);
    }
};

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
        tournamentName.textContent = tournament.name + " (ID: " + tournament.tournamentId + ")";

        const tournamentDates = document.createElement('div');
        tournamentDates.className = 'tournament-dates';

        const startDate = new Date(tournament.start);
        const endDate = new Date(tournament.end);
        const dateOptions = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };

        tournamentDates.textContent = `From: ${startDate.toLocaleDateString(undefined, dateOptions)} - To: ${endDate.toLocaleDateString(undefined, dateOptions)}`;

        const editButton = document.createElement('button');
        editButton.className = 'edit-btn';
        editButton.style.backgroundColor = '#fec007';
        editButton.textContent = 'Edit Tournament';
        editButton.onclick = function() {
            showEditForm(tournament);
        };

        const matrixButton = document.createElement('button');
        matrixButton.className = 'edit-btn';
        matrixButton.style.backgroundColor = '#2196F3';
        matrixButton.style.marginLeft = '10px';
        matrixButton.textContent = 'Ranking';
        matrixButton.onclick = function() {
            window.location.href = `matrix_table_admin.html?tournamentId=${tournament.tournamentId}&tournamentName=${encodeURIComponent(tournament.name)}`;
        };

        const deleteButton = document.createElement('button');
        deleteButton.className = 'delete-btn';
        deleteButton.textContent = 'Delete Tournament';
        deleteButton.onclick = function() {
            if (confirm('Are you sure you want to delete this tournament?')) {
                deleteTournament(tournament.tournamentId);
            }
        };

        tournamentItem.appendChild(tournamentName);
        tournamentItem.appendChild(tournamentDates);
        tournamentItem.appendChild(editButton);
        tournamentItem.appendChild(matrixButton);
        tournamentItem.appendChild(deleteButton);

        tournamentList.appendChild(tournamentItem);
    });
}

function createTournament() {
    const tournamentName = document.getElementById('tournament-name').value;
    const startDate = document.getElementById('start-date').value;
    const startTime = document.getElementById('start-time').value;
    const endDate = document.getElementById('end-date').value;
    const endTime = document.getElementById('end-time').value;
    const successMessage = document.getElementById('success-message');
    const errorMessage = document.getElementById('error-message');

    successMessage.style.display = 'none';
    errorMessage.style.display = 'none';

    if (!tournamentName || !startDate || !startTime || !endDate || !endTime) {
        errorMessage.textContent = 'Please fill in all fields';
        errorMessage.style.display = 'block';
        return;
    }

    const formattedStartTime = `${startDate} ${startTime}:00`;
    const formattedEndTime = `${endDate} ${endTime}:00`;

    const dateRegex = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;
    if (!dateRegex.test(formattedStartTime) || !dateRegex.test(formattedEndTime)) {
        errorMessage.textContent = 'Invalid date or time format';
        errorMessage.style.display = 'block';
        return;
    }

    fetch(`http://localhost:8080/tournaments/newtournament/${encodeURIComponent(tournamentName)}/${encodeURIComponent(formattedStartTime)}/${encodeURIComponent(formattedEndTime)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server responded with status: ${response.status}`);
            }
            return response.text();
        })
        .then(result => {
            console.log("Tournament creation result:", result);

            successMessage.style.display = 'block';

            document.getElementById('tournament-name').value = '';
            document.getElementById('start-date').value = '';
            document.getElementById('start-time').value = '';
            document.getElementById('end-date').value = '';
            document.getElementById('end-time').value = '';

            refreshUserData();
        })
        .catch(error => {
            errorMessage.textContent = `Error: ${error.message}`;
            errorMessage.style.display = 'block';
        });
}

function refreshUserData() {
    const userData = sessionStorage.getItem('userData');
    if (!userData) return;

    try {
        const dataArray = JSON.parse(userData);
        const user = dataArray[0];

        fetch(`http://localhost:8080/persons/getpersonenc/${user.username}/${user.password}`)
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
function showEditForm(tournament) {
    const editForm = document.getElementById('edit-tournament-form');
    const editTournamentId = document.getElementById('edit-tournament-id');
    const editTournamentName = document.getElementById('edit-tournament-name');
    const editStartDate = document.getElementById('edit-start-date');
    const editStartTime = document.getElementById('edit-start-time');
    const editEndDate = document.getElementById('edit-end-date');
    const editEndTime = document.getElementById('edit-end-time');

    document.getElementById('edit-success-message').style.display = 'none';
    document.getElementById('edit-error-message').style.display = 'none';

    editTournamentId.value = tournament.tournamentId;

    editTournamentName.value = tournament.name;

    const startDate = new Date(tournament.start);
    editStartDate.value = startDate.toISOString().split('T')[0];
    editStartTime.value = `${String(startDate.getHours()).padStart(2, '0')}:${String(startDate.getMinutes()).padStart(2, '0')}`;

    const endDate = new Date(tournament.end);
    editEndDate.value = endDate.toISOString().split('T')[0];
    editEndTime.value = `${String(endDate.getHours()).padStart(2, '0')}:${String(endDate.getMinutes()).padStart(2, '0')}`;

    editForm.style.display = 'block';

    editForm.scrollIntoView({ behavior: 'smooth' });
}

function cancelEdit() {
    document.getElementById('edit-tournament-form').style.display = 'none';
}

function updateTournament() {
    const tournamentId = document.getElementById('edit-tournament-id').value;
    const tournamentName = document.getElementById('edit-tournament-name').value;
    const startDate = document.getElementById('edit-start-date').value;
    const startTime = document.getElementById('edit-start-time').value;
    const endDate = document.getElementById('edit-end-date').value;
    const endTime = document.getElementById('edit-end-time').value;
    const successMessage = document.getElementById('edit-success-message');
    const errorMessage = document.getElementById('edit-error-message');

    successMessage.style.display = 'none';
    errorMessage.style.display = 'none';

    if (!tournamentName || !startDate || !startTime || !endDate || !endTime) {
        errorMessage.textContent = 'Please fill in all fields';
        errorMessage.style.display = 'block';
        return;
    }

    const startTimestamp = new Date(`${startDate}T${startTime}`).getTime();
    const endTimestamp = new Date(`${endDate}T${endTime}`).getTime();

    fetch(`http://localhost:8080/tournaments/altertournament/${tournamentId}/${encodeURIComponent(tournamentName)}/${startTimestamp}/${endTimestamp}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server responded with status: ${response.status}`);
            }
            return response.text();
        })
        .then(result => {
            console.log("Tournament update result:", result);

            successMessage.style.display = 'block';

            refreshUserData();

            setTimeout(() => {
                document.getElementById('edit-tournament-form').style.display = 'none';
            }, 2000);
        })
        .catch(error => {
            errorMessage.textContent = `Error: ${error.message}`;
            errorMessage.style.display = 'block';
        });
}


function deleteTournament(tournamentId) {
    const tournamentItems = document.querySelectorAll('.tournament-item');
    tournamentItems.forEach(item => {
        if (item.querySelector('.tournament-name').textContent.includes(`ID: ${tournamentId}`)) {
            item.remove();
        }
    });

    const remainingTournaments = document.querySelectorAll('.tournament-item');
    if (remainingTournaments.length === 0) {
        document.getElementById('no-tournaments').style.display = 'block';
    }

    fetch(`http://localhost:8080/tournaments/deletetournament/${tournamentId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server responded with status: ${response.status}`);
            }
            return response.text();
        })
        .then(result => {
            console.log("Tournament deletion result:", result);

            const tempMessage = document.createElement('div');
            tempMessage.className = 'success-message';
            tempMessage.style.display = 'block';
            tempMessage.textContent = 'Tournament deleted successfully!';
            document.getElementById('tournaments-card').appendChild(tempMessage);

            setTimeout(() => {
                if (tempMessage.parentNode) {
                    tempMessage.parentNode.removeChild(tempMessage);
                }
            }, 3000);

            refreshUserData();
        })
        .catch(error => {
            console.error("Error deleting tournament:", error);

            const tempMessage = document.createElement('div');
            tempMessage.className = 'error-message';
            tempMessage.style.display = 'block';
            tempMessage.textContent = `Error deleting tournament: ${error.message}`;
            document.getElementById('tournaments-card').appendChild(tempMessage);

            setTimeout(() => {
                if (tempMessage.parentNode) {
                    tempMessage.parentNode.removeChild(tempMessage);
                }
            }, 3000);
        });
}

function logout() {
    sessionStorage.removeItem('userData');

    window.location.href = 'login.html';
}