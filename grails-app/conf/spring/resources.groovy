// Place your Spring DSL code here
beans = {
	customObjectMarshallers( uk.ac.ox.brc.greenlight.marshaller.CustomObjectMarshallers ) {
		marshallers = [
				new   uk.ac.ox.brc.greenlight.marshaller.AttachmentMarshaller(),
				new   uk.ac.ox.brc.greenlight.marshaller.ConsentFormMarshaller()
		]
	}

	//use local GormTokenStorageService
	//as we need to manage token expiry
	tokenStorageService(uk.ac.ox.brc.greenlight.GormTokenStorageService) {
		userDetailsService = ref('userDetailsService')
	}
}
