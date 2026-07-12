<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="common/Head.jsp"/>
    <title>Política de Privacidad</title>
</head>

<body>

<jsp:include page="common/Navigation.jsp"/>

<div class="container" style="min-height:450px;">

    <div class="page-header">
        <h2>Política de Privacidad</h2>
    </div>

    <p>
        Esta aplicación recopila datos personales únicamente para el registro
        y autenticación de los usuarios.
    </p>

    <h3>Datos recopilados</h3>

    <ul>
        <li>Nombre</li>
        <li>Usuario</li>
        <li>Correo electrónico</li>
    </ul>

    <h3>Finalidad</h3>

    <p>
        Los datos serán utilizados exclusivamente para permitir el acceso
        a la aplicación y administrar la cuenta del usuario.
    </p>

    <h3>Protección de datos</h3>

    <p>
        La información será tratada conforme a la Ley Orgánica de Protección
        de Datos Personales (LOPDP) del Ecuador y será protegida mediante
        mecanismos de seguridad adecuados.
    </p>

    <h3>Derechos del titular</h3>

    <p>
        El usuario podrá solicitar el acceso, rectificación, actualización
        o eliminación de sus datos personales cuando corresponda.
    </p>
    <p>
        Al registrarse en la aplicación, el usuario manifiesta su consentimiento
        libre, específico, informado e inequívoco para el tratamiento de sus datos
        personales conforme a la Ley Orgánica de Protección de Datos Personales
        (LOPDP) del Ecuador.
    </p>

    <br>

    <a href="register" class="btn btn-primary">
        Volver al registro
    </a>

</div>

<jsp:include page="common/Footer.jsp"/>

</body>
</html>