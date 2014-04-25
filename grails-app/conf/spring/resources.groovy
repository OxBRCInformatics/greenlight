// Place your Spring DSL code here
beans = {
	customObjectMarshallers( uk.ac.ox.brc.greenlight.marshaller.CustomObjectMarshallers ) {
		marshallers = [
				new   uk.ac.ox.brc.greenlight.marshaller.AttachmentMarshaller(),
				new   uk.ac.ox.brc.greenlight.marshaller.ConsentFormMarshaller()
		]
	}
}
