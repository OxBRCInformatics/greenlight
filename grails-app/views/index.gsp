<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="mainBootstrap"/>
		<title>ORB Consent Form</title>

	</head>
	<body>

		<div id="page-body" role="main">
			<h1>Welcome to Grails</h1>




            <div id="controller-list" class="col-md-6">
				<h2>Available Controllers:</h2>



					<g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">



                            <g:link controller="${c.logicalPropertyName}" style="text-decoration:none">

                                <button type="button" class="btn btn-primary   btn-block " style="margin-bottom:5px;">${c.fullName}</button>


                            </g:link>


                    </g:each>



			</div>
		</div>
	</body>
</html>
