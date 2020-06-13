function validateRegister() {
    const password = document.getElementById("register_password").value;
    const confirmPassword = document.getElementById("register_confirm_password").value;

    if (password === confirmPassword) {
        return true;
    } else {
        document.getElementById("error_message").innerText = "Passwords are not match";
        document.getElementById("register_password").value = "";
        document.getElementById("register_confirm_password").value = "";
        return false;
    }
}