// Configuración
const API_CRITERIO = "http://localhost:8089/Criterio";
const API_VARIABLES = "http://localhost:8089/DatoSolicitado";

// 1. AL CARGAR LA PÁGINA: Traer variables de la BD
document.addEventListener('DOMContentLoaded', async () => {
    try {
        // Llamamos al endpoint que creaste: /Criterio/variables
        // Asegúrate de tener ese método en tu Controller
        const response = await fetch(`${API_URL}/variables`);
        if (!response.ok) throw new Error("Error al cargar variables");

        const variables = await response.json();

        llenarSelects(variables);

    } catch (error) {
        console.error(error);
        alert("Error: No se pudo conectar con el Backend para traer las variables.");
    }
});

// Función auxiliar para llenar los <select> del HTML
function llenarSelects(listaVariables) {
    const selectMulti = document.getElementById('selectVariablesBD');
    const selectSujeto = document.getElementById('varSujeto');

    listaVariables.forEach(variable => {
        // Opción para el multiselect
        // Asumo que tu objeto DatoSolicitado tiene 'id_dato' y 'nombre'
        let option1 = new Option(variable.nombre, variable.id_dato);
        selectMulti.add(option1);

        // Opción para el constructor de reglas (usamos el nombre técnico o normal)
        // Nota: Para la expresión, quizás prefieras usar el nombre interno
        let option2 = new Option(variable.nombre, variable.nombre);
        selectSujeto.add(option2);
    });
}

// 2. CAMBIO DE MODO (Visual)
function cambiarModo() {
    const tipo = document.getElementById('tipoCalculo').value;
    const panelEst = document.getElementById('panelEstadistico');
    const panelPart = document.getElementById('panelParticular');
    const labelRef = document.getElementById('labelRef');

    if (tipo === 'PARTICULAR') {
        panelEst.style.display = 'none';
        panelPart.style.display = 'block';
    } else {
        panelEst.style.display = 'flex'; // flex para que se vea bien el input-group
        panelPart.style.display = 'none';
        labelRef.innerText = (tipo === 'PROMEDIO') ? 'AVG Global' : 'MEDIANA Global';
    }
}

// 3. AL ENVIAR EL FORMULARIO (Guardar)
document.getElementById('formCriterio').addEventListener('submit', async (e) => {
    e.preventDefault(); // Evita que se recargue la página

    // A. Construir la Expresión Lógica
    const tipo = document.getElementById('tipoCalculo').value;
    let expresionFinal = "";

    if (tipo === 'PARTICULAR') {
        expresionFinal = document.getElementById('expresionManual').value;
    } else {
        const sujeto = document.getElementById('varSujeto').value;
        const op = document.getElementById('operadorEst').value;
        const func = (tipo === 'PROMEDIO') ? 'AVG' : 'MEDIAN';
        expresionFinal = `${sujeto} ${op} ${func}`;
    }

    // B. Obtener IDs de las variables seleccionadas (Multiselect)
    const selectMulti = document.getElementById('selectVariablesBD');
    const idsSeleccionados = Array.from(selectMulti.selectedOptions).map(opt => parseInt(opt.value));

    // C. Armar el objeto JSON (CriterioRequest)
    const datosEnviar = {
        nombre: document.getElementById('nombre').value,
        nombreStata: document.getElementById('nombreStata').value,
        leyenda: document.getElementById('leyenda').value,
        tipoCalculo: tipo,
        expresion: expresionFinal,
        variablesIds: idsSeleccionados // Aquí va la lista de IDs para la relación ManyToMany
    };

    console.log("Enviando:", datosEnviar);

    // D. Enviar al Backend
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datosEnviar)
        });

        if (response.ok) {
            const respuestaJson = await response.json();
            alert(`¡Éxito! Criterio creado con ID: ${respuestaJson.id_criterio}`);
            // Opcional: limpiar formulario
            document.getElementById('formCriterio').reset();
        } else {
            alert("Hubo un error al guardar. Revisa la consola.");
        }
    } catch (error) {
        console.error("Error de red:", error);
        alert("Error de conexión con el servidor.");
    }
});