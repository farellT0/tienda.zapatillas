// validaciones por expresiones regulares
// ^ empieza, $ termina, \p{L} = cualquier letra (con tildes, ñ...)
const regexp_nombre = /^\p{L}+(?:[ '\-]\p{L}+)*$/u

function validarNombre(nombre){
    nombre = (nombre || "").trim()
    if (nombre.length < 2 || nombre.length > 40 || !regexp_nombre.test(nombre)) {
        alert(typeof T !== "undefined" ? T.nombre_invalido
            : "El nombre solo puede tener letras y espacios, de 2 a 40 caracteres.")
        return false
    }
    return true
}
