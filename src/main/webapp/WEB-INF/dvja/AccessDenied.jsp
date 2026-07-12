<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="common/Head.jsp"/>
    <title>Acceso denegado</title>
</head>
<body>

<jsp:include page="common/Navigation.jsp"/>

<div class="container" style="min-height: 450px;">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">

            <div class="page-header">
                <h2>Acceso denegado</h2>
            </div>

            <div class="alert alert-danger">
                No tiene permisos suficientes para acceder a este recurso.
            </div>

            <p>
                El acceso está restringido de acuerdo con el rol asignado al usuario.
            </p>

            <a href="<s:url action='home'/>" class="btn btn-primary">
                Volver al inicio
            </a>

        </div>
    </div>
</div>

<jsp:include page="common/Footer.jsp"/>

</body>
</html>