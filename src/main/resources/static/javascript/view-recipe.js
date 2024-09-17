var modal = document.getElementById("imageModal");
var thumbnail = document.getElementById("imageThumbnail");

thumbnail.onclick = function(){
  modal.style.display = "block";
}

window.onclick = function(event) {
  if (event.target == modal) {
    modal.style.display = "none";
  }
}