// these vars relate to delete user modal
var modal = document.getElementById("delete-modal");
var modalBtn = document.getElementById("modal-btn");
var closeBtn = document.getElementById("modal-close");
// these vars relate to updating password
var authType = document.getElementById("auth-type").innerHTML;
var updateBtn = document.getElementById("update-btn");
var updatePassDiv = document.getElementById("update-fields");
var password = document.getElementById("password");
var confPassword = document.getElementById("confirm-password");
// this one is to display the error if a user clicks update password for a Google auth account
var googleError = document.getElementById("google-error");

modalBtn.onclick = function() {
  modal.style.display = "block";
}

closeBtn.onclick = function() {
  modal.style.display = "none";
}

window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}

updateBtn.onclick = function() {
  if (authType === "FORM") {
    updatePassDiv.style.display = "block";
    password.required = true;
    confPassword.required = true;
  }
  if (authType === "GOOGLE"){
    googleError.style.display = "block";
  }
}

function validatePassword(){
    confPassword.setCustomValidity(password.value != confPassword.value ? "Passwords Don't Match" : '');
}
password.onchange = validatePassword;
confPassword.onkeyup = validatePassword;