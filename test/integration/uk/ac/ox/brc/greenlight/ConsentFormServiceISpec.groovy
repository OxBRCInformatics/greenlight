package uk.ac.ox.brc.greenlight

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll

import javax.xml.bind.ValidationException

/**
 * Created by soheil on 28/03/2014.
 */
class ConsentFormServiceISpec extends IntegrationSpec {

    def   consentFormService
    def   consentEvaluationService

    def setup() {
		consentFormService.CDRService = Mock(CDRService)
        def attachment= new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),
                attachmentType: Attachment.AttachmentType.IMAGE, content: []).save()

		def question1 =  new Question(name: 'I read1...')
		def question2 =  new Question(name: 'I read2...')
		def question3 =  new Question(name: 'I read3...')
		def question4 =  new Question(name: 'I read4...')

        def template=new ConsentFormTemplate(
                id: 1,
                name: "ORB1",
                templateVersion: "1.1",
                namePrefix: "GNR")
				.addToQuestions(question1)
				.addToQuestions(question2)
				.addToQuestions(question3)
				.addToQuestions(question4)
        .save()


        def patient= new Patient(
            givenName: "Eric",
            familyName: "Clapton",
            dateOfBirth: new Date("30/03/1945"),
            hospitalNumber: "1002",
            nhsNumber: "1234567890",
            consents: []
            ).save(flush: true)

       def consent = new ConsentForm(
			   accessGUID: UUID.randomUUID().toString(),
                attachedFormImage: attachment,
                template: template,
                patient: patient,
                consentDate: new Date([year:2014,month:01,date:01]),
                consentTakerName: "Edward",
                formID: "GEN12345",
                formStatus: ConsentForm.FormStatus.NORMAL,
                comment: "a simple unEscapedComment, with characters \' \" \n "
        )
        consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question1))
        consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question2))
        consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question3))
        consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question4))
        patient.addToConsents(consent)
		patient.save(flush:true)


		def attachment2= new Attachment(id: 1, fileName: 'a.jpg', dateOfUpload: new Date(),
				attachmentType: Attachment.AttachmentType.IMAGE, content: []).save()

		def patient2= new Patient(
				givenName: "Eric",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567800",
				consents: []
		).save(flush:true)

		def consent2 = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment2,
				template: template,
				patient: patient2,
				consentDate: new Date([year:2014,month:02,date:02]),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				comment: "a simple unEscapedComment, entered at 31/07/2015 with characters \' \" \n "
		)
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question1))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question2))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question3))
		consent2.addToResponses(new Response(answer: Response.ResponseValue.YES,question: question4))
		patient2.addToConsents(consent2)
		patient2.save(flush:true)
    }

    def "Delete action will delete consentForm and its responses"() {

		given:"A number of consentForms are available"
		assert ConsentForm.count() == 2
		def cons = ConsentForm.first()
		assert cons.responses.size() == 4
		assert Response.count() == 8

		when:"deleting a consentForm"
        consentFormService.delete(cons)


        then:"the consentForm and its responses are all deleted"
		1 * consentFormService.CDRService.removeConsentForm(cons.patient,cons) >> { return [success:true,log:"SUCCESS"] }

		ConsentForm.count() == 1
        Response.count() == 4

		and:"it keeps the patient record"
        Patient.count() == 1
		Attachment.count() == 1
    }

	def "Delete action will delete patient if it was the only consentForm for that patient"() {

		given:"A number of consentForms are available"
		def attachment= new Attachment(attachmentType: Attachment.AttachmentType.IMAGE, content: [],dateOfUpload:new Date(),fileName:"1.jpg").save(flush: true,failOnError: true)
		def consent = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment,
				template: ConsentFormTemplate.first(),
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN12345"
		)
		def patient = Patient.first()
		patient.addToConsents(consent).save(flush:true)

		assert Patient.count()     == 2
		assert ConsentForm.count() == 3
		assert Attachment.count()  == 3
		when:"deleting a consentForm"
		consentFormService.delete(patient.consents[0])


		then:"the consentForm, attachment will be removed but not the patient as it still has more consent forms"
		1 * consentFormService.CDRService.removeConsentForm(patient,patient.consents[0]) >> { return [success:true,log:"SUCCESS"] }
		ConsentForm.count() == 2
		Patient.count() 	== 2
		Attachment.count()  == 2


		when:"deleting a consentForm"
		consentFormService.delete(patient.consents[0])

		then:"the consentForm, attachment and the patient will be removed as that's the only consent for the patient"
		1 * consentFormService.CDRService.removeConsentForm(patient,patient.consents[0]) >> { return [success:true,log:"SUCCESS"] }
		ConsentForm.count() == 1
		Patient.count() 	== 1
		Attachment.count()  == 1
	}

	def "Delete action will delete consentForm and its responses even if CDRService calls fails (Saving the delete in CDR)"() {

		given:"A number of consentForms are available"
		assert ConsentForm.count() == 2
		def cons = ConsentForm.first()
		assert cons.responses.size() == 4
		assert Response.count() == 8

		when:"deleting a consentForm"
		consentFormService.delete(cons)


		then:"the consentForm and its responses are all deleted"
		1 * consentFormService.CDRService.removeConsentForm(cons.patient,cons) >> { throw new ValidationException("ERROR IN CDR CALL") }

		ConsentForm.count() == 1
		Response.count() == 4

		and:"it keeps the patient record"
		Patient.count() == 1
		Attachment.count() == 1
	}

    def "Check getConsentFormByFormId for not-available FormId "() {
        when:"CheckFormId is called for a non-existing formId"
        def formId = "123"
        def consentId = consentFormService.getConsentFormByFormId(formId);

        then:"then -1 will be returned as not available"
        consentId == -1
    }


    def "Check getConsentFormByFormId for available FormId "() {
        when:"CheckFormId is called for an existing formId"
        def expectedConsentId = ConsentForm.list()[0].id
        def consentId = consentFormService.getConsentFormByFormId(ConsentForm.list()[0].formID);

        then:"then the actual consent id should be returned"
        consentId != -1
        consentId == expectedConsentId
    }


    def "getConsentFormByFormId will not return a specific Id for general FormId (ends with 00000)"() {
        when:"CheckFormId is called for a general FormId"
        def formId = "GEN00000"
        def consentId = consentFormService.getConsentFormByFormId(formId);

        then:"then it returns -1"
        consentId == -1
    }



    def "exportAllConsentFormsToCSV returns CSV content with Headers"() {
        when:"exportAllConsentFormsToCSV is called"
        String csv = consentFormService.exportAllConsentFormsToCSV()
        csv.readLines().size() != 0
        def headers=csv.readLines()[0].tokenize(",")

        then:"the first row is header"
        headers.size() == 14
        headers[0] == "consentId"
        headers[1] == "consentDate"
        headers[2] == "consentformID"
        headers[3] == "consentTakerName"
        headers[4] == "formStatus"
        headers[5] == "patientNHS"
        headers[6] == "patientMRN"
        headers[7] == "patientName"
        headers[8] == "patientSurName"
        headers[9] =="patientDateOfBirth"
        headers[10] == "templateName"
        headers[11] == "consentResult"
        headers[12] == "responses"
        headers[13] == "comments"
    }


    def "exportToCSV returns consent in CSV format"() {
        given: "something"
        def expectedConsents = []
        ConsentForm.list().each { consentForm ->
            expectedConsents.add([
                    consentForm.id as String,
                    consentForm.consentDate.format("dd-MM-yyyy"),
                    consentForm.formID as String,
                    consentForm.consentTakerName,
                    consentForm.formStatus as String,
                    consentForm.patient.nhsNumber,
                    consentForm.patient.hospitalNumber,
                    consentForm.patient.givenName,
                    consentForm.patient.familyName,
                    consentForm.patient.dateOfBirth.format("dd-MM-yyyy"),
                    consentForm.template.namePrefix,
					consentForm.consentStatus as String,
                    consentForm.responses.collect { it.answer as String }.join('|'),
                    getCSVEscapedComment(consentForm.comment)
            ].join(','))
        }

        when: "we export the CSV content"
        String csv = consentFormService.exportAllConsentFormsToCSV()
        def csvConsents = csv.split('\n').toList()
        csvConsents.remove(0) // remove the header row

        then: "the exported content matches our expectations"
        expectedConsents.size() == csvConsents.size()
        expectedConsents == csvConsents
    }

    private String getCSVEscapedComment(String unEscapedComment) {

        String escapedDblQuote = "\""
        String comment = unEscapedComment.replaceAll("\n","\t")
        comment = comment.replaceAll(escapedDblQuote, escapedDblQuote + escapedDblQuote)
        comment = escapedDblQuote + comment + escapedDblQuote

        return  comment
    }

	def "search will return consent form based on the specified nhsNumber as search criteria"() {

		when: "search is called with specified nhsNumber as search criteria"
		def param = [:]
		param.nhsNumber = nhsNmber
		def result  = consentFormService.search(param)

		then:"returns result"
		result.size() == count

		where:
		nhsNmber		|	count
		"1234567890"	|	  1
		""				|	  2
	}


	def "findAndExport will return search result as CSV string"() {

		when: "exportConsentFormSearchResultToCSV is called with specified nhsNumber as search criteria"
		def param = [:]
		param.nhsNumber = "1234567890"
		def resultCSVString  = consentFormService.findAndExport(param)
		def lines = resultCSVString.split("\n")

		then:"returns result"
		lines[0].contains("consentId,consentDate,consentformID,consentTakerName,formStatus,patientNHS,patientMRN,patientName,patientSurName,patientDateOfBirth,templateName,consentResult,responses,comments")
		lines[1].contains("01-02-3914,GEN12345,Edward,Normal,1234567890,1002,Eric,Clapton,03-06-1947,GNR,No consent,Yes|Yes|Yes|Yes,\"a simple unEscapedComment, with characters ' \"\"\"")
	}


	void "Save will update the ConsentForm if it is in update mode"(){

		given:"An attachment is annotated"
		def attachment = Attachment.first()
		//check if it is already annotated
		!attachment.consentForm
		attachment?.consentForm.patient.givenName = "A.."
		attachment?.consentForm.consentTakerName  = "B.."

		when:"save is called for annotating the same attachment again"
		def result  = consentFormService.save(attachment.consentForm.patient,attachment.consentForm)
		def consent = Attachment.first()?.consentForm

		then:
		1 * consentFormService.CDRService.saveOrUpdateConsentForm(_,_,_) >>{return "success"}
		result
		consent.consentTakerName  == "B.."
		consent.patient.givenName == "A.."
	}


	void "Save will save ConsentForm,patient and responses for an unAnnotated attachment"(){

		given:"An unAnnotated attachment is available & get annotated"
		def attachment= new Attachment(id: 200, fileName: 'a.jpg', dateOfUpload: new Date(),
				attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush:true)
		def template = ConsentFormTemplate.first()

		def patient= new Patient(
				givenName: "Eric",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		)
		def consent = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment,
				template: template,
				patient: patient,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				comment: "a simple unEscapedComment, with characters \' \" \n "
		)
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[0]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[1]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[2]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[3]))

		def consentBefore = ConsentForm.count()
		def responseBefore = Response.count()
		def patientBefore = Patient.count()

		when:"save is called"
		def result  = consentFormService.save(patient,consent)

		then:"it will save consentForm,patient and responses"
		1 * consentFormService.CDRService.saveOrUpdateConsentForm(_,_,_) >> {return "success"}
		result
		ConsentForm.count() == consentBefore + 1
		Response.count() == responseBefore + 4
		Patient.count() == patientBefore + 1
	}

	void "Save will save ConsentForm and patient even if CDR is not available or throws exception"(){

		given:"An unAnnotated attachment is available & get annotated"
		def attachment= new Attachment(id: 200, fileName: 'a.jpg',attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush:true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def patient  = new Patient(nhsNumber: "1234567890",hospitalNumber: "OLD")
		def consentForm = new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.SPOILED, formID: "GEL56890",accessGUID: "123", template:template, patient:patient,attachedFormImage: attachment, consentDate: new Date())
		//they are not saved
		assert !patient.id
		assert !consentForm.id

		when:"save is called"
		def result  = consentFormService.save(patient,consentForm)

		then:"it will save consentForm and patient even if CDR throws exception"
		1 * consentFormService.CDRService.saveOrUpdateConsentForm(_,_,_) >> {throw new RuntimeException("Failed to connect to CDR")}
		result
		//they are saved
		assert patient.id
		assert consentForm.id
	}

	void "Save will save ConsentForm and patient and pass consent into CDR"(){

		given:"An unAnnotated attachment is available & get annotated"
		def attachment= new Attachment(id: 200, fileName: 'a.jpg',attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush:true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def patient  = new Patient(nhsNumber: "1234567890",hospitalNumber: "OLD")
		def consentForm = new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.SPOILED, formID: "GEL56890",accessGUID: "123", template:template, patient:patient,attachedFormImage: attachment, consentDate: new Date())
		//they are not saved
		assert !patient.id
		assert !consentForm.id

		when:"save is called"
		def result  = consentFormService.save(patient,consentForm)

		then:"it will save consentForm and patient and pass consent into CDR"
		1 * consentFormService.CDRService.saveOrUpdateConsentForm(_,_,_) >> {[succes:true,log:"Successfully passed to CDR!"]}
		result
		//they are saved
		assert patient.id
		assert consentForm.id
	}

	void "Save will calculate and update ConsentStatus attribute of ConsentForm in Save mode"(){

		given:"An unAnnotated attachment is available & get annotated"
		def attachment	= new Attachment(id: 200, fileName: 'a.jpg', dateOfUpload: new Date(),attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flush:true)
		def template 	= ConsentFormTemplate.first()

		def patient = new Patient(
				givenName: "Eric",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567890",
				consents: []
		)
		def consent = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment,
				template: template,
				patient: patient,
				consentDate: new Date([year:2014,month:01,date:01]),
				consentTakerName: "Edward",
				formID: "GEN12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				comment: "a simple unEscapedComment, with characters \' \" \n "
		)
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[0]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[1]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[2]))
		consent.addToResponses(new Response(answer: Response.ResponseValue.YES,question: template.questions[3]))

		//check if it is NON_CONSENT Before saving
		assert consent.consentStatus == ConsentForm.ConsentStatus.NON_CONSENT

		when:"save is called"
		def result  = consentFormService.save(patient,consent)

		then:"it will update consentStatus attribute of the ConsentForm object"
		1 * consentFormService.CDRService.saveOrUpdateConsentForm(_,_,_) >>{return "success"}
		result
		!consent.hasErrors()
		consent.consentStatus == ConsentForm.ConsentStatus.FULL_CONSENT
	}

	void "Save will calculate and update ConsentStatus of the ConsentForm in update mode"(){

		given:"An attachment is annotated"
		def attachment = Attachment.first()
		//Form is NOT Consented before update
		assert attachment?.consentForm?.consentStatus == ConsentForm.ConsentStatus.NON_CONSENT

		//Responses are changed to NO
		attachment?.consentForm?.responses?.each { response ->
			response.answer = Response.ResponseValue.YES
		}

		when:"save is called for annotating the same attachment again"
		def result  = consentFormService.save(attachment.consentForm.patient,attachment.consentForm)

		then:
		1 * consentFormService.CDRService.saveOrUpdateConsentForm(_,_,_) >>{return "success"}
		result
		attachment?.consentForm?.consentStatus == ConsentForm.ConsentStatus.FULL_CONSENT
	}

	void "Save will calculate and update ConsentStatus of the ConsentForm in update mode if FormTemplate is even updated"(){

		given:"An attachment is annotated"
		def attachment	= Attachment.first()
		attachment?.consentForm?.consentStatus = ConsentForm.ConsentStatus.NON_CONSENT
		attachment.save(failOnError: true)
		//Form is NOT Consented before update
		assert attachment?.consentForm?.consentStatus == ConsentForm.ConsentStatus.NON_CONSENT

		//Attachment is Updated with a different type of Consent Form Template ..........................
		def newTemplate = new ConsentFormTemplate(id:30, name: "ORB1",templateVersion: "1.1",namePrefix: "CDR")
				.addToQuestions(new Question(name: 'I read1...'))
				.addToQuestions(new Question(name: 'I read2...'))
				.addToQuestions(new Question(name: 'I read3...'))
				.addToQuestions(new Question(name: 'I read4...'))
				.save()
		attachment?.consentForm?.template = newTemplate
		attachment.consentForm.addToResponses(new Response(answer: Response.ResponseValue.YES,question: newTemplate.questions[0]))
		attachment.consentForm.addToResponses(new Response(answer: Response.ResponseValue.YES,question: newTemplate.questions[1]))
		attachment.consentForm.addToResponses(new Response(answer: Response.ResponseValue.YES,question: newTemplate.questions[2]))
		attachment.consentForm.addToResponses(new Response(answer: Response.ResponseValue.YES,question: newTemplate.questions[3]))
		//...............................................................................................

		when:"save is called for annotating the same attachment again"
		def result  = consentFormService.save(attachment.consentForm.patient,attachment.consentForm)

		then:
		1 * consentFormService.CDRService.saveOrUpdateConsentForm(_,_,_) >>{return "success"}
		result
		attachment?.consentForm?.template.id == newTemplate.id
		attachment?.consentForm?.consentStatus == ConsentForm.ConsentStatus.FULL_CONSENT
	}

	@Unroll
	def "search will also search on text in comment (for comment:\"#comment\" & nshNumber:\"#nhsNumber\" " () {

		when: "search is called to search for a text in comment"
		def param = [:]
		param.comment = comment
		param.nhsNumber = nhsNumber
		def result  = consentFormService.search(param)

		then:"returns result"
		result.size() == count

		where:
		nhsNumber		|			comment				|	count
		""				|	  "simple unEscapedComment"	|	2
		""				|	  "SIMPLE UNEscapedComment"	|	2
		""				|	  "a sim"					|	2
		""				|	  "31/07/2015"				|	1
		""				|	  "with characters ' \""	|	2
		""				|		"happy!"				|   0
		""				|		""						|   2
		""				|		null					|   2
		"1234567890"	|	 "simple unEscapedComment"	|	1
	}

	@Unroll
	def "searchByAccessGUID returns consentForm based on accessGUID for #accessGUID"(){

		given:"we have a consentForm with specific GUID"
		def aConsent = ConsentForm.list()[0]
		aConsent.accessGUID = "c3a25672-cd1f-4ad5-886c-6916572a8ae7"
		aConsent.save(failOnError: true)

		when:"searchByAccessGUID is called"
		def result = consentFormService.searchByAccessGUID(accessGUID)

		then:"It returns the right consentForm"
		result?.formID == resultformID

		where:
		accessGUID								|	resultformID
		"c3a25672-cd1f-4ad5-886c-6916572a8ae7"	|	"GEN12345"
		"A"										|	null
		null									|	null
	}


	def "findConsentsOfSameTypeAfterThisConsentWhichAreSavedInCDR "(){
		given:"a patient with several consents exists"
		def patient  = new Patient(nhsNumber: "0987654321",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56890",accessGUID: UUID.randomUUID().toString(), template:template, patient:patient,consentDate: new Date())
		patient.addToConsents(consentForm)
		patient.save(flush: true,failOnError: true)

		def samePatient = new Patient(nhsNumber: "0987654321",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		samePatient.addToConsents(new ConsentForm(savedInCDR: true,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56891",accessGUID: UUID.randomUUID().toString(), template:template, patient:patient,consentDate: new Date().plus(1)))
		samePatient.addToConsents(new ConsentForm(savedInCDR: true,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56892",accessGUID: UUID.randomUUID().toString(), template:template, patient:patient,consentDate: new Date().plus(2)))
		samePatient.addToConsents(new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56893",accessGUID: UUID.randomUUID().toString(), template:template, patient:patient,consentDate: new Date().plus(2)))
		samePatient.save(flush: true,failOnError: true)

		when:
		def result = consentFormService.findConsentsOfSameTypeAfterThisConsentWhichAreSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm)

		then:"it returns consentForms of the same type saved in cdr which are newer that ConsentForm"
		result.size() == 2
	}

	def "findAnyConsentOfSameTypeBeforeThisConsentWhichIsSavedInCDR "(){
		given:"a patient with several consents exists"
		def patient  = new Patient(nhsNumber: "0987654321",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56890",accessGUID: UUID.randomUUID().toString(), template:template, patient:patient,consentDate: new Date())
		patient.addToConsents(consentForm)
		patient.save(flush: true,failOnError: true)

		def samePatient = new Patient(nhsNumber: "0987654321",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def cons = new ConsentForm(savedInCDR: true,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56891",accessGUID: UUID.randomUUID().toString(), template:template, patient:patient,consentDate: new Date().minus(1))
		samePatient.addToConsents(cons)
		samePatient.save(flush: true,failOnError: true)

		when:
		def result = consentFormService.findAnyConsentOfSameTypeBeforeThisConsentWhichIsSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm)

		then:"it returns any consentForm of the same type saved in cdr which is older that ConsentForm"
		result.id == cons.id
	}


	def "findLatestConsentOfSameTypeBeforeThisConsentWhichIsNotSavedInCDR "(){
		given:"a patient with several consents exists"
		def patient  = new Patient(nhsNumber: "0987654321",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def template = new ConsentFormTemplate(name:"temp1",namePrefix:"TEMP",templateVersion: "V1" ).save(failOnError: true,flush: true)
		def consentForm = new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56890",accessGUID: UUID.randomUUID().toString(), template:template, patient:patient,consentDate: new Date())
		patient.addToConsents(consentForm)
		patient.save(flush: true,failOnError: true)

		def samePatient = new Patient(nhsNumber: "0987654321",hospitalNumber: "OLD").save(failOnError: true,flush: true)
		def latestCons = new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56891",accessGUID: UUID.randomUUID().toString(), template:template, patient:patient,consentDate: new Date().minus(1))
		samePatient.addToConsents(latestCons)
		samePatient.addToConsents(new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56892",accessGUID: UUID.randomUUID().toString(), template:template, patient:patient,consentDate: new Date().minus(2)))
		samePatient.addToConsents(new ConsentForm(savedInCDR: false,formStatus: ConsentForm.FormStatus.NORMAL, formID: "GEL56893",accessGUID: UUID.randomUUID().toString(), template:template, patient:patient,consentDate: new Date().plus(2)))
		samePatient.save(flush: true,failOnError: true)

		when:"findConsentsOfSameTypeAfterThisConsentWhichAreSavedInCDR called"
		def result = consentFormService.findLatestConsentOfSameTypeBeforeThisConsentWhichIsNotSavedInCDR(patient.nhsNumber,patient.hospitalNumber,consentForm,template)

		then:
		result.id == latestCons.id
	}
}