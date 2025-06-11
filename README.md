# 🖥️ Clínica Salud Total - Aplicación de Escritorio

Proyecto desarrollado como parte de la materia **Metodología de Sistemas I** de la **Tecnicatura Universitaria en Programación** en **ITG - UTN Resistencia**.

---

## 🧑‍💻 Autor
- **Juan Gabriel Pared**  
- 2° año  
- Profesor: **Marcos Sosa**

---

## 📌 Descripción

Esta aplicación de escritorio forma parte del sistema de gestión de la clínica **“Salud Total”**. Está diseñada para ser utilizada por personal administrativo (como secretaría) y permite gestionar pacientes, turnos, disponibilidad médica e informes administrativos. La aplicación consume servicios desde la **API RESTful** desarrollada en Java Spring Boot.

La interfaz fue desarrollada con **Kotlin Multiplatform + Compose Desktop**, enfocándose en ofrecer una experiencia ágil, funcional y conectada al backend clínico.

---

## 🛠️ Tecnologías utilizadas

- Kotlin Multiplatform
- Compose Desktop
- IntelliJ IDEA
- Consumo de API REST (Spring Boot)
- JSON serialization
- MySQL (base de datos del backend)
- Arquitectura MVC modular

---

## 📚 Funcionalidades implementadas

### 👤 Gestión de Pacientes
- Registro de nuevos pacientes.
- Edición y eliminación de pacientes existentes.
- Visualización del historial de turnos por paciente.

### 🗓️ Gestión de Turnos
- Asignación de turnos por **especialidad** y **profesional**.
- Visualización de turnos futuros y pasados.
- Cancelación y reprogramación de turnos.

### 🧑‍⚕️ Control de Disponibilidad
- Consulta de horarios disponibles por profesional.
- Visualización de la agenda médica diaria.

### 📊 Informes de Gestión
- **Cantidad de turnos atendidos por profesional** en un período determinado.
- **Reporte de turnos cancelados y reprogramados**.
- Informes personalizados definidos por la clínica (Ej: Tasa de cancelación por especialidad, distribución de turnos por área médica, etc.).

---

## 🚀 Estructura del Proyecto
com.clinica
├── login # Pantalla de login (secretaria)
├── screens # Pantallas de cada módulo (Inicio, Pacientes, Turnos, etc.)
├── models # Modelos de datos (DTOs y entidades)
├── services # Servicios REST para consumir la API
├── utils # Funciones de ayuda, validaciones, etc.
└── Main.kt # Punto de entrada de la aplicación

---

## ⚙️ Requisitos previos

- JDK 17+
- IntelliJ IDEA con plugin de Kotlin y Compose Multiplatform
- API REST corriendo en `http://localhost:8080/api`
- MySQL 8+ con la base de datos de la clínica previamente cargada

---

## ▶️ Cómo ejecutar

1. Clonar este repositorio:
   ```bash
   git clone https://github.com/tu-usuario/clinica-escritorio.git
   cd clinica-escritorio
2. Abrir el proyecto con IntelliJ.
3. Asegurarse de que la API está corriendo y la base de datos está activa.
4. Ejecutar la clase Main.kt para iniciar la aplicación.

---

**##🔗 Comunicación con la API**
La app de escritorio se comunica directamente con los endpoints RESTful expuestos por la API.
Ejemplos de consumo:
/api/pacientes → Listado y búsqueda de pacientes
/api/turnos/profesional/{id}/disponibilidad → Consulta de disponibilidad
/api/turnos/reportes/turnos-atendidos → Informes por profesional
