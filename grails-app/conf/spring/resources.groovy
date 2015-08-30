// Place your Spring DSL code here
beans = {
	customObjectMarshallers( uk.ac.ox.brc.greenlight.marshaller.CustomObjectMarshallers ) {
		marshallers = [
				new   uk.ac.ox.brc.greenlight.marshaller.AttachmentMarshaller(),
				new   uk.ac.ox.brc.greenlight.marshaller.ConsentFormMarshaller(),
				new   uk.ac.ox.brc.greenlight.marshaller.ResponseMarshaller(),
				new	  uk.ac.ox.brc.greenlight.marshaller.QuestionMarshaller(),
				new	  uk.ac.ox.brc.greenlight.marshaller.ConsentFormTemplateMarshaller()

		]
	}

	//use local GormTokenStorageService
	//as we need to manage token expiry
	tokenStorageService(uk.ac.ox.brc.greenlight.GormTokenStorageService) {
		userDetailsService = ref('userDetailsService')
	}
}
