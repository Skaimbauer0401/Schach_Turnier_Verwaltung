document.getElementById('register-btn').addEventListener('click', function() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const adminkey = document.getElementById('adminkey').value || "notadmin";
    const errorMessage = document.getElementById('error-message');
    const successMessage = document.getElementById('success-message');

    // Hide any previous messages
    errorMessage.style.display = 'none';
    successMessage.style.display = 'none';

    // Validate inputs
    if (!username || !password || !confirmPassword) {
        errorMessage.textContent = 'Please fill in all required fields';
        errorMessage.style.display = 'block';
        return;
    }

    // Check if passwords match
    if (password !== confirmPassword) {
        errorMessage.textContent = 'Passwords do not match';
        errorMessage.style.display = 'block';
        return;
    }

    // Disable the button to prevent multiple requests
    const registerBtn = document.getElementById('register-btn');
    registerBtn.disabled = true;

    // Show loading message
    errorMessage.textContent = 'Processing registration, please wait...';
    errorMessage.style.display = 'block';

    // Make API call to register the user
    let fetchString = `http://localhost:8080/persons/newperson/${username}/${password}/${adminkey}`;

    fetch(fetchString)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server responded with status: ${response.status}`);
            }
            return response.text();
        })
        .then(data => {
            // Hide loading message
            errorMessage.style.display = 'none';

            // Process the response
            console.log("Response received:", data);

            if (data === "Benutzername bereits vergeben") {
                errorMessage.textContent = 'Username already taken';
                errorMessage.style.display = 'block';
            } else if (data === "1") {
                // Registration successful
                successMessage.textContent = 'Registration successful! You can now login.';
                successMessage.style.display = 'block';

                // Clear the form
                document.getElementById('username').value = '';
                document.getElementById('password').value = '';
                document.getElementById('confirm-password').value = '';
                document.getElementById('adminkey').value = '';

                // Redirect to login page after 3 seconds
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 3000);
            } else {
                errorMessage.textContent = `Registration failed: ${data}`;
                errorMessage.style.display = 'block';
            }
        })
        .catch(error => {
            // Display error message
            errorMessage.textContent = `Error: ${error.message}`;
            errorMessage.style.display = 'block';
        })
        .finally(() => {
            // Re-enable the button
            registerBtn.disabled = false;
        });
});

// Back to login button
document.getElementById('back-to-login-btn').addEventListener('click', function() {
    window.location.href = 'login.html';
});