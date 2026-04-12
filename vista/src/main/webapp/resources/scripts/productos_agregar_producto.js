function toggleChip(el) {
  if (el.className == "chip seleccionado") {
    el.className = "chip";
  } else {
    el.className = "chip seleccionado";
  }
}

function toggleProv(el) {
  if (el.className.indexOf("seleccionado") != -1) {
    el.className = "proveedor-btn";
  } else {
    el.className = "proveedor-btn seleccionado";
  }
}
