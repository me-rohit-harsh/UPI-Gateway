 const closeButtons = document.querySelectorAll(".alert-container .btn-close");

 closeButtons.forEach(function (button) {
     button.addEventListener("click", function () {
         const alert = this.closest(".alert");
         alert.style.display = "none";
     });
 });