document.getElementById('register-btn').addEventListener('click', function() {
    window.location.href = 'registration.html';
});

document.getElementById('login-btn').addEventListener('click', function() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('error-message');

    if (!username || !password) {
        errorMessage.textContent = 'Please enter both username and password';
        errorMessage.style.display = 'block';
        return;
    }

    errorMessage.style.display = 'none';


    errorMessage.textContent = 'Loading data, please wait...';
    errorMessage.style.display = 'block';

    const loginBtn = document.getElementById('login-btn');
    loginBtn.disabled = true;

    fetch(`http://localhost:8080/persons/getperson/${username}/${password}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server responded with status: ${response.status}`);
            }
            return response.text();
        })
        .then(text => {
            if(text === "Benutzername oder Passwort falsch") {
                errorMessage.textContent = text;
                errorMessage.style.display = 'block';
                return null;
            } else if(text === "SQL Fehler") {
                errorMessage.textContent = text;
                errorMessage.style.display = 'block';
                return null
            }else{
                return JSON.parse(text);
            }
        })
        .then(data => {
            if (data === null) {
                return;
            }

            errorMessage.style.display = 'none';

            console.log("Data received:", data);

            sessionStorage.setItem('userData', JSON.stringify(data));

            if (data[0].admin) {
                window.location.href = 'dashboard_admin.html';
            } else {
                window.location.href = 'dashboard.html';
            }
        })
        .catch(error => {
            errorMessage.textContent = `Error: ${error.message}`;
            errorMessage.style.display = 'block';
        })
        .finally(() => {
            loginBtn.disabled = false;
        });
});
