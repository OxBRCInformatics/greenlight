import grails.converters.JSON
import grails.converters.XML
import org.codehaus.groovy.grails.web.converters.configuration.ChainedConverterConfiguration
import org.codehaus.groovy.grails.web.converters.configuration.ConvertersConfigurationHolder
import org.codehaus.groovy.grails.web.converters.configuration.DefaultConverterConfiguration
import org.springframework.jca.cci.CciOperationNotSupportedException
import org.springframework.web.context.support.WebApplicationContextUtils
import uk.ac.ox.brc.greenlight.Attachment
import uk.ac.ox.brc.greenlight.ConsentForm
import uk.ac.ox.brc.greenlight.ConsentFormTemplate
import uk.ac.ox.brc.greenlight.Patient
import uk.ac.ox.brc.greenlight.Question
import uk.ac.ox.brc.greenlight.Response
import uk.ac.ox.brc.greenlight.auth.AppRole
import uk.ac.ox.brc.greenlight.auth.AppUser
import uk.ac.ox.brc.greenlight.auth.UserRole

class BootStrap {

	def databaseCleanupService

    def init = { servletContext ->

		def springContext = WebApplicationContextUtils.getWebApplicationContext(servletContext)
		springContext.getBean( "customObjectMarshallers" ).register()

		//Using grails.plugins.rest.client.RestBuilder constructor re-initialises JSON response rendering.
		//Any custom renderers will subsequently not be used.
		//https://jira.grails.org/browse/GRAILS-11801
		//And we faced this exception:
		//org.codehaus.groovy.grails.web.converters.exceptions.ConverterException: Error converting Bean with class org.springframework.beans.GenericTypeAwarePropertyDescriptor
		//BUT it seems that the following code solves this problem:
		//http://grails.1312388.n4.nabble.com/Marshallers-are-blowing-up-in-2-3-5-anyone-else-td4653954.html#a4657521
		DefaultConverterConfiguration<JSON> cfg = (DefaultConverterConfiguration<JSON>)ConvertersConfigurationHolder.getConverterConfiguration(JSON)
		ConvertersConfigurationHolder.setDefaultConfiguration(JSON.class, new ChainedConverterConfiguration<JSON>(cfg, cfg.proxyHandler));


		//We need this code, otherwise Grails will return dates in a default format and
		//for example for sth like 25-12-2015 it will return 24-12-2015T23:00:00 and it was problematic
		//this line will fix it
//		JSON.registerObjectMarshaller(Date) {
//			return it?.format("dd-MM-yyyy HH:mm:ss")
//		}
//		XML.registerObjectMarshaller(Date) {
//			return it?.format("dd-MM-yyyy HH:mm:ss")
//		}

        environments {
            test {
                createRoles()
                createAdminUser("admin", "password", "support@example.com")
				createAPIUser("api","api","api@api.cpm")
				createFormTemplates()
            }
            development {
                createRoles()
                createAdminUser("admin", "password", "support@example.com")
				createAPIUser("api","api","api@api.cpm")
                createFormTemplates()
				//createTestUser("test","test","test@test.com")
				//addConsentFormForDevelopment()
            }

            production {
                createRoles()
                createAdminUser("admin", "password", "support@example.com")
                createFormTemplates()
            }
        }
    }

    def createRoles(){
        AppRole.findByAuthority('ROLE_ADMIN') ?: new AppRole(authority: 'ROLE_ADMIN').save(failOnError: true)
        AppRole.findByAuthority('ROLE_USER') ?: new AppRole(authority: 'ROLE_USER').save(failOnError: true)
        AppRole.findByAuthority('ROLE_API') ?: new AppRole(authority: 'ROLE_API').save(failOnError: true)
    }

    def createAdminUser(String username, String password, String email){
        if(!AppUser.findByUsername(username) ){
            def user = new AppUser(username: username, enabled: true, emailAddress: email, password: password).save(failOnError: true)
            UserRole.create user, AppRole.findByAuthority('ROLE_ADMIN')
        }
    }

	def createAPIUser(String username, String password, String email){
		if(!AppUser.findByUsername(username) ){
			def user = new AppUser(username: username, enabled: true, emailAddress: email, password: password).save(failOnError: true)
			UserRole.create user, AppRole.findByAuthority('ROLE_API')
		}
	}

	def createTestUser(String username, String password, String email){
		if(!AppUser.findByUsername(username) ){
			def user = new AppUser(username: username, enabled: true, emailAddress: email, password: password).save(failOnError: true)
			UserRole.create user, AppRole.findByAuthority('ROLE_USER')
		}
	}

    def createFormTemplates(){

        if(ConsentFormTemplate.count()==0)
        {
            new ConsentFormTemplate(
                    name: "ORB General Consent Form",
                    namePrefix: "GEN",
                    templateVersion: "v1 October 2013",
					cdrUniqueId : "ORB_GEN_V1"
            ).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 1 dated December 2013). I have had the opportunity to ask questions and have had these answered satisfactorily.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree to give samples for research and/or allow samples already collected as part of my medical care to be used by the biobank.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for the biobank during the course of my hospital care.  I understand that I will be asked for permission each time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I understand that my participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of my samples that have not already been used in research.  Withdrawing from the biobank will not affect my present and future medical care and legal rights in any way.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my health care records for research that uses my samples.  I understand that the biobank will keep my information confidential. Information will only be passed on to researchers in a form that protects my identity.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return incidental findings", name: 'I understand that results from research tests on my samples might be medically important to me.  I agree to my hospital consultant and GP being informed, and that research findings that are important for treating serious medical conditions I may have can be discussed with me.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I understand and agree that my samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, I would not profit financially.  ',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any medical research that has research ethics committee approval. I understand that future laboratory research may use new tests or techniques that are not yet known.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I understand that relevant sections of my medical notes and data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my research records.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my samples may be used in genetic research aimed at understanding the genetic influences on disease and that the results of these investigations are unlikely to have any implications for me personally.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not contact", name: 'I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).save(failOnError: true)
        }




        if(ConsentFormTemplate.count()==1)
        {
            new ConsentFormTemplate(
                    name: "ORB Specific Programme Clinically Relevant Genomics - Oncology Consent Form for Adults",
                    namePrefix: "CRA",
                    templateVersion: "v1 October 2013",
					cdrUniqueId : "ORB_CRA_V1"
            ).addToQuestions(new Question(optional: true, name: 'I have read and understood the information sheet for this study (Version 1 dated October 2013). I have had the opportunity to ask questions and have had these answered satisfactorily.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree to give samples for research and/or allow samples already collected as part of my medical care to be used by the biobank.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for the biobank during the course of my hospital care.  I understand that I will be asked for permission each time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I understand that my participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of my samples that have not already been used in research.  Withdrawing from the biobank will not affect my present and future medical care and legal rights in any way.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my health care records for research that uses my samples.  I understand that the biobank will keep my information confidential. Information will only be passed on to researchers in a form that protects my identity.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I understand and agree that my samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, I would not profit financially.'  ,validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any medical research that has research ethics committee approval. I understand that future laboratory research may use new tests or techniques that are not yet known.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I understand that relevant sections of my medical notes and data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my research records.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my samples may be used in genetic research aimed at understanding the genetic influences on disease.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return clinically relevant results", name: 'Clinically relevant results: I agree that findings from genetic and other testing related to the reason I am currently undergoing investigations will be fed back to my clinician so that they may be used in decisions about my treatment.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return incidental findings", name: 'Incidental findings: I understand and agree that I will be informed of any results of genetic analysis of my sample where they are NOT relevant to the condition being investigated, but are judged to be important for my/my family’s health care, and can be acted upon medically.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not contact", name: 'I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).save(failOnError: true)
        }


		if(ConsentFormTemplate.count()==2)
		{
			new ConsentFormTemplate(
					name: "100,000 Genomes Project (Pilot) – Cancer Sequencing Consent Form",
					namePrefix: "GEL",
					templateVersion: "Version 1.0 dated 25.08.2014", //"Version 1.0 dated  25.08.2014"
					cdrUniqueId : "GEL_CSC_V1"
			).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 1.0 dated 25.08.2014). I have had the opportunity to ask questions and have had these answered satisfactorily.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that my participation is voluntary, that I am free to withdraw at any time without giving a reason, and that withdrawing will not affect my present or future medical care and legal rights in any way.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree to provide samples and/or allow samples already collected as part of my medical care to be used for this research.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that further samples may be taken for this study during the course of my hospital care, if necessary.  I understand that I will be asked for permission each time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that my donated samples will be used to collect DNA from blood and from any cancer specimen for whole genome sequencing. This genetic information may be used in research aimed at understanding the genetic influences that contribute to cancer development and responses to treatment. Samples may also be used to study proteins and other structures.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that my DNA sequence and anonymised clinical data will be deposited ultimately in a secure database held by Genomics England, where they can be accessed by approved investigators from the public or private sectors, for scientific or clinical purposes.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I consider these samples a gift to the Oxford University Hospitals NHS Trust and understand that I will not gain any direct personal or commercial benefit as a result of taking part in this project. I will also not gain any benefit from any other future research undertaken as a result of this gift.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that research study staff can collect and store securely information from my health care records, now and in the future. I understand that the study researchers will keep my information confidential. Information will only be passed on in a form that protects my identity.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree to my GP being contacted and asked to share information about my medical history and to give access to medical records.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that data collected during the study may be looked at by authorised individuals from Oxford University Hospitals NHS Trust, Genomics England Ltd, and other study monitors where it is relevant to my taking part in this research. I permit them to access my medical records.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that if information is discovered from genetic and other testing related to the reason I am currently undergoing investigations, this will be fed back to my clinician and may be discussed with me regarding its use in decisions about my treatment. I understand that it is not yet known how long it would take to receive such results.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand and agree that I will NOT be informed of any results of genetic analysis of my sample where these are not relevant to the management of my current condition.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree to be contacted in future about this study and other ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).save(failOnError: true)
		}


		//Update Old Questions (optional and labelIfNotYes fields) for those two ConsentForms
		if(ConsentFormTemplate.count() == 2)
		{
			def GENConsent = ConsentFormTemplate.findByNamePrefix("GEN")
			if(GENConsent != null) {
				Question q1 = GENConsent.questions.find {it.name=="I have read and understood the information sheet for this study (Version 1 dated December 2013). I have had the opportunity to ask questions and have had these answered satisfactorily."}
				if(q1){
					q1.optional = true
					q1.save(flush: true)
				}

				Question q6 = GENConsent.questions.find{it.name=="I understand that results from research tests on my samples might be medically important to me.  I agree to my hospital consultant and GP being informed, and that research findings that are important for treating serious medical conditions I may have can be discussed with me."}
				if(q6){
					q6.optional = true
					q6.labelIfNotYes = "Do not return incidental findings"
					q6.save(flush: true)
				}

				Question q11= GENConsent.questions.find{it.name=="I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies."}
				if(q11){
					q11.optional = true
					q11.labelIfNotYes ="Do not contact"
					q11.save(flush: true)
				}
			}

			def CRAConsent = ConsentFormTemplate.findByNamePrefix("CRA")
			if (CRAConsent != null){
				Question q1= CRAConsent.questions.find{it.name=="I have read and understood the information sheet for this study (Version 1 dated October 2013). I have had the opportunity to ask questions and have had these answered satisfactorily."}
				if(q1){
					q1.optional = true
					q1.save(flush: true)
				}

				Question q10 = CRAConsent.questions.find{ it.name=="Clinically relevant results: I agree that findings from genetic and other testing related to the reason I am currently undergoing investigations will be fed back to my clinician so that they may be used in decisions about my treatment."}
				if(q10)
				{
					q10.optional = true
					q10.labelIfNotYes = "Do not return clinically relevant results"
					q10.save(flush: true)
				}


				Question q11 = CRAConsent.questions.find{ it.name=="Incidental findings: I understand and agree that I will be informed of any results of genetic analysis of my sample where they are NOT relevant to the condition being investigated, but are judged to be important for my/my family’s health care, and can be acted upon medically."}
				if(q11)
				{
					q11.optional = true
					q11.labelIfNotYes = "Do not return incidental findings"
					q11.save(flush: true)
				}

				Question q12 = CRAConsent.questions.find{ it.name=="I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies."}
				if(q12)
				{
					q12.optional = true
					q12.labelIfNotYes = "Do not contact"
					q12.save(flush: true)
				}
			}
		}


        if(ConsentFormTemplate.count()==3)
        {
            new ConsentFormTemplate(
                    name: "100,000 Genomes Project (Pilot) – Cancer Sequencing Consent Form",
                    namePrefix: "GEL",
                    templateVersion: "Version 2 dated 14.10.2014",//"Version 2 dated 14.10.14"
					cdrUniqueId : "GEL_CSC_V2"
			).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 1.0 dated 25.08.2014). I have had the opportunity to ask questions and have had these answered satisfactorily.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I understand that my participation is voluntary, that I am free to withdraw at any time without giving a reason, and that withdrawing will not affect my present or future medical care and legal rights in any way.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree to provide samples and/or allow samples already collected as part of my medical care to be used for this research.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree that further blood samples may be taken for this study during the course of my hospital care, if necessary. I understand that I will be asked for permission each time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree that the tissue and blood samples I give can be used, stored and distributed for use for research, including genetic testing and sequencing of the whole genome. This genetic information may be used in research aimed at understanding the genetic influences that contribute to cancer development and responses to treatment.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I understand that my DNA sequence and anonymised clinical data will be deposited ultimately in a secure database held by Genomics England, where they can be accessed in anonymised form by approved investigators from the public or private sectors, for scientific or clinical purposes.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I consider these samples a gift to the Oxford University Hospitals NHS Trust and understand that I will not gain any direct financial benefit as a result of taking part in this project. I will also not gain any financial benefit from any other future research undertaken as a result of this gift.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree that research study staff can collect and store securely information from my health care records, now and in the future. I understand that the study researchers will keep my information confidential. Information will only be passed on in a form that protects my identity.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree to my GP being contacted and asked to share information about my medical history and to give access to medical records.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I understand that data collected during the study may be looked at by authorised individuals from Oxford University Hospitals NHS Trust, Genomics England, and other study monitors where it is relevant to my taking part in this research. I permit them to access my medical records.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree that if information is discovered from genetic and other testing related to the reason I am currently undergoing investigations, this will be fed back to my clinician and may be discussed with me regarding its use in decisions about my treatment. I understand that it is not yet known how long it would take to receive such results.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I understand and agree that I will NOT be informed of any results of genetic analysis of my sample where these are not relevant to the management of my current condition.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).addToQuestions(new Question(name: 'I agree to be contacted in future about this study and other ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
            ).save(failOnError: true)
        }

		if(ConsentFormTemplate.count()==4)
		{
			new ConsentFormTemplate(
					name: "Pre-2014 ORB consent form",
					namePrefix: "PRE",
					templateVersion: "Version 1.2 dated 03.03.2009", //"Version 1.2 dated 3rd March 2009"
					cdrUniqueId : "ORB_PRE_V1_2"
			).addToQuestions(new Question(name: 'I have read and understood the patient information sheet (green v1.2 dated 3rd March 2009). My questions have been answered satisfactorily. I know how to contact the research team.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree to give a sample of blood and/or other tissues for research.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for research during the course of my hospital care. I understand that I will be asked for permission each time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand how the samples will be taken, that participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of my samples providing they have not already been used in research.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my health care records for research that uses my samples. I understand the biobank will keep my information confidential. Information will only be passed to researchers in an anonymous way that protects my identity.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand results from research tests on my samples might be medically important to me. I agree to my hospital consultant and GP being informed and that relevant experimental findings can be discussed with me.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree to gift blood samples taken for the purpose of the research study to the University of Oxford. If a commercial product were developed as a result of this study, I will not profit financially from such a product.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any medical research that has research ethics committee approval. I understand that future laboratory research may use new tests or techniques that are not yet known.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'Consent for genetic research: I understand that my samples may be used in genetic research aimed at understanding the genetic influences on diseases and that the results of these investigations are unlikely to have any implications for me personally.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that relevant sections of my medical notes and data collected by ORB, may be looked at by individuals from Oxford University, from regulatory authorities or from the NHS Trust, where it is relevant to my taking part in this research. I give permission for these individuals to have access to my records.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).save(failOnError: true)
		}



		if(ConsentFormTemplate.count()==5)
		{
			new ConsentFormTemplate(
					name: "ORB General Consent Form",
					namePrefix: "GEN",
					templateVersion: "v2 April 2014",
					cdrUniqueId : "ORB_GEN_V2"
			).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 1 dated December 2013). I have had the opportunity to ask questions and have had these answered satisfactorily.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree to give samples for research and/or allow samples already collected as part of my medical care to be used by the biobank.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for the biobank during the course of my hospital care.  I understand that I will be asked for permission each time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that my participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of my samples that have not already been used in research.  Withdrawing from the biobank will not affect my present and future medical care and legal rights in any way.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my health care records for research that uses my samples.  I understand that the biobank will keep my information confidential. Information will only be passed on to researchers in a form that protects my identity.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return incidental findings", name: 'I understand that results from research tests on my samples might be medically important to me.  I agree to my hospital consultant and GP being informed, and that research findings that are important for treating serious medical conditions I may have can be discussed with me.',validResponses: [Response.ResponseValue.NO,Response.ResponseValue.YES,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.NO)
			).addToQuestions(new Question(name: 'I understand and agree that my samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, I would not profit financially.  ',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any medical research that has research ethics committee approval. I understand that future laboratory research may use new tests or techniques that are not yet known.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that relevant sections of my medical notes and data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my research records.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my samples may be used in genetic research aimed at understanding the genetic influences on disease and that the results of these investigations are unlikely to have any implications for me personally.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not contact", name: 'I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).save(failOnError: true)
		}


		//update consent form version label
		if(ConsentFormTemplate.count() == 6){
			//databaseCleanupService.updateConsentTemplateVersion()
			//databaseCleanupService.updateCDRUniqueId()

			//UPDATE GEL ConsentName
			def gelV1 = ConsentFormTemplate.findByCdrUniqueId("GEL_CSC_V1")
			gelV1.name = "100,000 Genomes Project (Pilot) – Cancer Sequencing Consent Form"
			gelV1.save(flush: true,failOnError: true)


			def gelV2 = ConsentFormTemplate.findByCdrUniqueId("GEL_CSC_V2")
			gelV2.name = "100,000 Genomes Project (Pilot) – Cancer Sequencing Consent Form"
			gelV2.save(flush: true,failOnError: true)
		}


		//add new consentForm GEL MAIN
		if (ConsentFormTemplate.count() == 6){

			new ConsentFormTemplate(
					name: "100,000 Genomes Project (Main) – Cancer Sequencing Consent Form",
					namePrefix: "GLM",
					templateVersion: "Version 2.0 dated 20.01.2015",
					cdrUniqueId : "GEL_MAN_V2"
			/* 1 */ ).addToQuestions(new Question(name: 'I understand that my participation in the 100,000 Genomes Project is voluntary and that if I refuse, I don’t need to give any reason and that my present or future medical care or legal rights will not be affected. I confirm that I have read and understood the information dated __/__/__ (version__) for the 100,000 Genomes Project. I have had the opportunity to ask questions and have had these answered satisfactorily.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 2 */ ).addToQuestions(new Question(name: 'The 100,000 Genomes Project allows medical researchers, healthcare teams and commercial organisations to access your samples and health and related information we collect. I agree to join this Project and specifically understand the following:',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 3 */ ).addToQuestions(new Question(name: 'I agree to give a sample of blood, or tissue, and/or for samples already collected as part of my medical care to be used, and if essential, saliva to be collected, and for details about me and any samples I provide to be stored securely by Genomics England.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 4 */ ).addToQuestions(new Question(name: 'I agree that my donated sample(s) can be used to collect DNA for whole genome sequencing, or for studies looking at proteins or other components of my cells. I understand that my samples may be processed in other ways that have approval from the 100,000 Genomes Project. I understand that my samples or DNA could be sent for secure processing or analysis outside of the UK by approved organisations. I understand that future research on my samples may use new tests or techniques that are not yet known.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 5 */ ).addToQuestions(new Question(name: 'I agree to give access to my health records and personal information to be used alongside my samples for scientific or medical purposes and research relating to medical condition(s) affecting me, or other people. I understand that this can be at any point during my life and will continue after my death.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 6 */ ).addToQuestions(new Question(name: 'I agree that my samples, my DNA sequence, and information from my healthcare records and any other information I give to the Project can be collected and stored securely by the Project as a resource, for use by approved researchers from around the world, for future scientific or medical purposes during my life and after my death. I understand that they won’t be allowed to copy or remove any of my information.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 7 */ ).addToQuestions(new Question(name: 'I agree that the 100,000 Genomes Project can collect, store and analyse information from my medical notes and health records (in general practice from my GP or hospital, or social care records, or other sources such as local or national disease registries). I consent to this access now and in the future (including after my death). I agree that the notes and records or the samples I give may be looked at by approved individuals from Genomics England or from the NHS and other study monitors at any time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 8 */ ).addToQuestions(new Question(name: 'I understand that all information about me held by the Project will be treated as confidential. I understand that information from my samples, records or other information I give to the Project will only be accessible in a form which protects my identity.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 9 */ ).addToQuestions(new Question(name: 'I understand that these research organisations could include commercial (for-profit) companies.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 10 */).addToQuestions(new Question(name: 'I understand that I will not benefit financially if research undertaken through the 100,000 Genomes Project leads to new treatments or medical tests.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 11 */).addToQuestions(new Question(name: 'If, based on results obtained from my samples and/or information about me, I might be eligible to participate in future research studies, I agree to be contacted by my clinical team in order to invite me to participate. This may be about this Project or other ethically approved research studies, including clinical trials or research about ethics. I will be provided with full information about these studies, when and if I am contacted. I understand that agreeing to be contacted does not oblige me to participate.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 12 */).addToQuestions(new Question(name: 'I agree to being asked to provide further samples or health information by my clinical care team for the purposes of the Project. I understand that I am not obliged to give this permission. I also agree to be contacted directly up to four times in any year by the Project (not via my doctor), for additional information about my health or lifestyle. I understand that this does not oblige me to participate.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 13 */).addToQuestions(new Question(name: 'I understand that it is not possible to guarantee that we will find anything of significance now or in the future.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 14 */).addToQuestions(new Question(name: 'Main genetic findings (non-optional): I give consent for the Project to run tests on my samples and health information relating to the cause or management of the main condition that led me to join the Project (sometimes called my ‘primary’ or ‘pertinent’ findings). I agree that these results can be reported to my extended clinical care team. I agree that my clinical care team can discuss these results with me. I also understand that the results may not be able to provide a diagnosis or to provide information to influence my clinical care, assuming that these results can be returned in a relevant time frame, which may not be possible.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 15 */).addToQuestions(new Question(optional: true, labelIfNotYes: "I do not want additional genetic results (secondary findings) to be looked for and fed back to my clinical team", name: 'Additional genetic findings (optional): I understand that I can choose whether or not I want the Project to use my samples and health information to look for additional genetic results, beyond my main findings (sometimes called my ‘secondary’ findings). The results would relate to my risk of developing certain serious or possibly life-threatening and rare medical conditions which can be cured, made less severe, or prevented via standard NHS treatment. The Project has agreed with the NHS, a limited list of the medical conditions to be looked for under this criteria. This list will be updated and may change over time according to new medical evidence. The person taking my consent has a list available of which genetic conditions will currently be looked for under these criteria. These conditions will be fully explained to me, along with the implications of a positive test, and the support that will be available to me if I then choose to have these tests. If I allow the Project to look for these additional findings, a health professional will discuss these results with me after they have been confirmed in the NHS.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO],defaultResponse: Response.ResponseValue.NO)
			/* 16 */).addToQuestions(new Question(name: 'Incidental findings: I understand and agree that any other results of genetic or other analysis of my samples (separate to the main and, if consented for, additional findings above) will not be fed back to me.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 17 */).addToQuestions(new Question(name: 'I understand that information generated by this Project may be of benefit to my family members now, or in future. I understand that normal clinical practice will be applied in the use and sharing of this information with other members of my family and their medical teams, based on my relative/s’ potential benefit from receiving this information.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			/* 18 */).addToQuestions(new Question(name: 'I understand that I am free to withdraw my permission for my samples and information to be used at any time in the future. I don’t need to give a reason and my medical care will not be affected. I understand that if I join and then withdraw from this Project, it will not be possible to remove my data from research that has already have taken place.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					).save(failOnError: true)

		}


		//add new consentForm GEL MAIN V2.1
		if (ConsentFormTemplate.count() == 7){

			new ConsentFormTemplate(
					name: "100,000 Genomes Project (Main) – Cancer Sequencing Consent Form",
					namePrefix: "GLM",
					templateVersion: "Version 2.1 dated 24.09.2015",
					cdrUniqueId : "GEL_MAN_V2_1"
					/* 1 */ ).addToQuestions(new Question(name: 'I have read and understood the participant information sheet ‘For adult patients with cancer (or suspected cancer)’ dated ________ (version ___ ) for the 100,000 Genomes Project. I have had the opportunity to ask questions and have had these answered satisfactorily. I understand that my participation in the 100,000 Genomes Project is voluntary and that if I decline, I don’t need to give any reason and that my present or future medical care or legal rights will not be affected.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 2 */ ).addToQuestions(new Question(name: 'Sample collection: I agree to give a sample of blood, and of tissue from my (suspected) cancer, and for samples already collected as part of my medical care to be used. I also agree to give other samples such as saliva if necessary. I agree to being asked by my clinical team to provide further samples in the future for the purposes of the Project. I understand that agreeing to be asked for further samples does not mean that I have to provide them. I agree that details about me and any samples I provide will be stored securely by Genomics England.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 3 */ ).addToQuestions(new Question(name: 'Use of samples: I agree that my donated sample(s) can be used to collect DNA for whole genome sequencing, and for studies looking at proteins or other components of my cells. I understand that my samples or DNA could be sent to approved organisations outside the UK for secure processing or analysis. I understand that future research on my samples may use new tests or techniques that are not yet known. ',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 4 */ ).addToQuestions(new Question(name: 'Use of health data: The 100,000 Genomes Project allows medical researchers, healthcare teams and commercial organisations to access samples and information collected by the Project. I agree that the Project can access, collect, store and analyse information from my medical notes, health records and personal information (from my GP or hospital or social care records, or other sources such as local or national disease registries), to be used alongside my samples for scientific or medical purposes and research relating to medical condition(s) affecting me or other people. I understand that this can be at any point during my life and will continue after my death, unless I have withdrawn from the Project. I understand that researchers won’t be allowed to copy or remove any of my information. I agree that these notes and records or the samples I give may be looked at by approved individuals from Genomics England or from the NHS Trust and other study monitors at any time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 5 */ ).addToQuestions(new Question(name: 'Confidentiality: I understand that all information about me held by the Project will be treated as confidential. I understand that information from my samples, records or other information I give to the Project will only be accessible to researchers other than my clinical team in a form that protects my identity. I understand that my GP and other healthcare professionals may be informed of my participation in the Project.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 6 */ ).addToQuestions(new Question(name: 'Access for commercial companies: I understand that the research organisations accessing the data could include commercial (for-profit) companies.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 7 */ ).addToQuestions(new Question(name: 'Financial implications: I understand that I will not benefit financially if research undertaken through the 100,000 Genomes Project leads to new treatments or medical tests.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 8 */ ).addToQuestions(new Question(name: 'Future contact: I agree to be contacted by my clinical team, or directly by the Project team for more information about my health, or to be invited to participate in future research studies. I understand that this research may be about this Project or other ethically approved research studies, including clinical trials or research about my experience of the Project. I understand that I will be provided with full information about these studies when and if I am contacted, and that agreeing to be contacted does not mean that I have to take part. ',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 9 */ ).addToQuestions(new Question(name: 'Main genetic findings (agreement to this is necessary to take part in the Project): I give consent for the Project to run tests on my samples and health information relating to the cause or management of my main condition, the (suspected) cancer that was the reason I was invited to join the Project. (Main findings are also sometimes called ‘primary’ or ‘pertinent’ findings). I agree that these results can be reported to my clinical team for them to discuss with me. I understand that the results may not be able to provide any information to help with my clinical care. I understand that the results may not be returned in a time frame which will allow them to be used in my clinical care.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 10 */).addToQuestions(new Question(optional: true, labelIfNotYes: "I do not want additional genetic results (secondary findings) to be looked for and fed back to my clinical team.", name: 'Health-related additional findings (optional): I understand that I can choose whether or not I want the Project to look in my genome data for additional genetic results, beyond my main findings. (Additional Findings are also sometimes called ‘secondary’ findings). I understand that if I choose to receive these findings, the Project will provide me with findings that are likely to benefit me because I may be offered screening or treatment as a result. However, I understand that there is still uncertainty about such findings and I may not benefit from receiving them. I understand that if no additional findings are found, I may still be at risk of the conditions they can cause. I understand that I can change my mind about whether to receive these results at any time by completing an Opt-in or Opt-out form.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO],defaultResponse: Response.ResponseValue.NO)
					/* 11 */).addToQuestions(new Question(optional: true, labelIfNotYes: "I do not want reproductive additional results (carrier findings) to be looked for and fed back to my clinical team.", name: 'Reproductive additional findings (carrier testing) (optional): I understand that I can choose whether or not to be tested to see if I ‘carry’ a risk of passing on serious or possibly life-threatening genetic conditions to my future children. These conditions may or may not be able to be cured, made less severe, or prevented via standard NHS treatment. I understand that I may still have a chance of having a child with one of the studied conditions, even if the additional findings analysis doesn’t identify anything in me. I understand that I can change my mind about whether to receive these results at any time by completing an Opt-in or Opt-out form.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.NOT_RELEVANT],defaultResponse: Response.ResponseValue.NO)
					/* 12 */).addToQuestions(new Question(name: 'Other findings: I understand that any other results of genetic or other analysis of my samples (which are not related to my main condition or additional findings) will not routinely be fed back to me.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 13 */).addToQuestions(new Question(name: 'Family members: I understand that information generated by this Project may be of benefit to my family members now or in the future. I understand that the clinical team will advise and support me with sharing this information with my family members if this is the case.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 14 */).addToQuestions(new Question(name: 'Withdrawing from the Project: I understand that I am free to withdraw my permission for my samples and information to be used at any time in the future. I don’t need to give a reason and my routine medical care will not be affected. I understand that if I join and then withdraw from this Project it will not be possible to remove my data or samples from research that may already have taken place.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).save(failOnError: true)

		}

		//add new consentForm GEL MAIN V2.2
		if (ConsentFormTemplate.count() == 8){

			new ConsentFormTemplate(
					name: "100,000 Genomes Project (Main) – Cancer Sequencing Consent Form",
					namePrefix: "GLM",
					templateVersion: "Version 2.2 dated 01.07.2016",
					cdrUniqueId : "GEL_MAN_V2_2"
					/* 1 */ ).addToQuestions(new Question(name: 'I have read and understood the participant information sheet ‘For adult patients with cancer (or suspected cancer)’ dated ________ (version ___ ) for the 100,000 Genomes Project. I have had the opportunity to ask questions and have had these answered satisfactorily. I understand that my participation in the 100,000 Genomes Project is voluntary and that if I decline, I don’t need to give any reason and that my present or future medical care or legal rights will not be affected.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 2 */ ).addToQuestions(new Question(name: 'Sample collection: I agree to give a sample of blood, and other samples such as saliva if necessary. I also agree for samples already collected as part of my medical care to be used, such as a piece of my tumour and/or bone marrow depending on my cancer type. I agree to being asked by my clinical team to provide further samples in the future for the purposes of the Project. I understand that agreeing to be asked for further samples does not mean that I have to provide them. I agree that details about me and any samples I provide will be stored securely by Genomics England.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 3 */ ).addToQuestions(new Question(name: 'Use of samples: I agree that my donated sample(s) can be used to collect DNA for whole genome sequencing, and for studies looking at proteins or other components of my cells. I understand that my samples or DNA could be sent to approved organisations outside the UK for secure processing or analysis. I understand that future research on my samples may use new tests or techniques that are not yet known. ',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 4 */ ).addToQuestions(new Question(name: 'Use of health data: The 100,000 Genomes Project allows medical researchers, healthcare teams and commercial organisations to access samples and information collected by the Project. I agree that the Project can access, collect, store and analyse information from my medical notes, health records and personal information (from my GP or hospital or social care records, or other sources such as local or national disease registries), to be used alongside my samples for scientific or medical purposes and research relating to medical condition(s) affecting me or other people. I understand that this can be at any point during my life and will continue after my death, unless I have withdrawn from the Project. I understand that researchers won’t be allowed to copy or remove any of my information. I agree that these notes and records or the samples I give may be looked at by approved individuals from Genomics England or from the NHS Trust and other study monitors at any time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 5 */ ).addToQuestions(new Question(name: 'Confidentiality: I understand that all information about me held by the Project will be treated as confidential. I understand that information from my samples, records or other information I give to the Project will only be accessible to researchers other than my clinical team in a form that protects my identity. I understand that my GP and other healthcare professionals may be informed of my participation in the Project.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 6 */ ).addToQuestions(new Question(name: 'Access for commercial companies: I understand that the research organisations accessing the data could include commercial (for-profit) companies.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 7 */ ).addToQuestions(new Question(name: 'Financial implications: I understand that I will not benefit financially if research undertaken through the 100,000 Genomes Project leads to new treatments or medical tests.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 8 */ ).addToQuestions(new Question(name: 'Future contact: I agree to be contacted by my clinical team, or directly by the Project team for more information about my health, or to be invited to participate in future research studies. I understand that this research may be about this Project or other ethically approved research studies, including clinical trials or research about my experience of the Project. I understand that I will be provided with full information about these studies when and if I am contacted, and that agreeing to be contacted does not mean that I have to take part. ',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 9 */ ).addToQuestions(new Question(name: 'Main genetic findings (agreement to this is necessary to take part in the Project): I give consent for the Project to run tests on my samples and health information relating to the cause or management of my main condition, the (suspected) cancer that was the reason I was invited to join the Project. (Main findings are also sometimes called ‘primary’ or ‘pertinent’ findings). I agree that these results can be reported to my clinical team for them to discuss with me. I understand that the results may not be able to provide any information to help with my clinical care. I understand that the results may not be returned in a time frame which will allow them to be used in my clinical care.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 10 */).addToQuestions(new Question(optional: true, labelIfNotYes: "I do not want additional genetic results (secondary findings) to be looked for and fed back to my clinical team.", name: 'Health-related additional findings (optional): I understand that I can choose whether or not I want the Project to look in my genome data for additional genetic results, beyond my main findings. (Additional Findings are also sometimes called ‘secondary’ findings). I understand that if I choose to receive these findings, the Project will provide me with findings that are likely to benefit me because I may be offered screening or treatment as a result. However, I understand that there is still uncertainty about such findings and I may not benefit from receiving them. I understand that if no additional findings are found, I may still be at risk of the conditions they can cause. I understand that I can change my mind about whether to receive these results at any time by completing an Opt-in or Opt-out form.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO],defaultResponse: Response.ResponseValue.NO)
					/* 11 */).addToQuestions(new Question(optional: true, labelIfNotYes: "I do not want reproductive additional results (carrier findings) to be looked for and fed back to my clinical team.", name: 'Reproductive additional findings (carrier testing) (optional): I understand that I can choose whether or not to be tested to see if I ‘carry’ a risk of passing on serious or possibly life-threatening genetic conditions to my future children. These conditions may or may not be able to be cured, made less severe, or prevented via standard NHS treatment. I understand that I may still have a chance of having a child with one of the studied conditions, even if the additional findings analysis doesn’t identify anything in me. I understand that I can change my mind about whether to receive these results at any time by completing an Opt-in or Opt-out form.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.NOT_RELEVANT],defaultResponse: Response.ResponseValue.NO)
					/* 12 */).addToQuestions(new Question(name: 'Other findings: I understand that any other results of genetic or other analysis of my samples (which are not related to my main condition or additional findings) will not routinely be fed back to me.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 13 */).addToQuestions(new Question(name: 'Family members: I understand that information generated by this Project may be of benefit to my family members now or in the future. I understand that the clinical team will advise and support me with sharing this information with my family members if this is the case.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
					/* 14 */).addToQuestions(new Question(name: 'Withdrawing from the Project: I understand that I am free to withdraw my permission for my samples and information to be used at any time in the future. I don’t need to give a reason and my routine medical care will not be affected. I understand that if I join and then withdraw from this Project it will not be possible to remove my data or samples from research that may already have taken place.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).save(failOnError: true)

		}

		//add new consentForm ORB Specific Programme Clinically Relevant Genomics - Oncology Parental Consent Form
		if (ConsentFormTemplate.count() == 9) {

			new ConsentFormTemplate(
					name: "ORB Specific Programme Clinically Relevant Genomics - Oncology Parental Consent Form",
					namePrefix: "CRP",
					templateVersion: "Version 2 dated April 2014",
					cdrUniqueId: "ORB_CRP_V2_0"
					/* 1 */).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 1 dated October 2013). I have had the opportunity to ask questions and have had these answered satisfactorily.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 2 */).addToQuestions(new Question(name: 'I agree to my child giving samples for research and/or allow samples already collected as part of my child’s medical care to be used by the biobank.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 3 */).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for the biobank during the course of my child’s hospital care. I understand that permission with be asked each time.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 4 */).addToQuestions(new Question(name: 'I understand that my child’s participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of their samples that have not already been used in research. Withdrawing from the biobank will not affect my child’s present and future medical care and legal rights in any way.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 5 */).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my child’s health care records for research that uses their samples. I understand that the biobank will keep my child’s information confidential. Information will only be passed on to researchers in a form that protects my child’s identity.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 6 */).addToQuestions(new Question(name: 'I understand and agree that my child’s samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, neither my child nor I would profit financially.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 7 */).addToQuestions(new Question(name: 'I give permission for the biobank to store my child’s samples and distribute them for use in any medical research that has research ethics committee approval. I understand that future laboratory research may use new tests or techniques that are not yet known.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 8 */).addToQuestions(new Question(name: 'I understand that relevant sections of my child’s medical notes and data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my child’s research records.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 9 */).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my child’s samples may be used in genetic research aimed at understanding the genetic influences on disease.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 10 */).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return clinically relevant results to patient's clinician.", name: "Clinically relevant results: I agree that findings from genetic and other testing related to the reason my child is currently undergoing investigations will be fed back to my child’s clinician so that they may be used in decisions about my child’s treatment.", validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO], defaultResponse: Response.ResponseValue.NO)
					/* 11 */).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return incidental findings to parents.", name: "Incidental findings: I understand and agree that I will be informed of any results of genetic analysis of my child’s sample where they are NOT relevant to the condition being investigated, but are judged to be important for my/my family’s health care, and can be acted upon medically.", validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO], defaultResponse: Response.ResponseValue.NO)
					/* 12 */).addToQuestions(new Question(optional: true, name: 'I agree to be contacted about ethically approved research studies for which my child may be suitable. I understand that agreeing to be contacted does not oblige my child to participate in any further studies.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
			).save(failOnError: true)

			//Update GENV1 consent first question and make it non-Optional as requested on 30,Jan,2017 by S.J
			def GENConsentV1 = ConsentFormTemplate.findByCdrUniqueId("ORB_GEN_V1")
			if (GENConsentV1 != null) {
				Question q1 = GENConsentV1.questions.find {
					it.name == "I have read and understood the information sheet for this study (Version 1 dated December 2013). I have had the opportunity to ask questions and have had these answered satisfactorily."
				}
				if (q1) {
					q1.optional = false
					q1.save(flush: true)
				}
			}

			//Update GENV1 consent first question and make it non-Optional as requested on 30,Jan,2017 by S.J
			def GENConsentV2 = ConsentFormTemplate.findByCdrUniqueId("ORB_GEN_V2")
			if (GENConsentV2 != null) {
				Question q1 = GENConsentV2.questions.find {
					it.name == "I have read and understood the information sheet for this study (Version 1 dated December 2013). I have had the opportunity to ask questions and have had these answered satisfactorily."
				}
				if (q1) {
					q1.optional = false
					q1.save(flush: true)
				}
			}


		}

		//add new consentForm GEL MAIN V2.3
		if (ConsentFormTemplate.count() == 10){

			new ConsentFormTemplate(
					name: "100,000 Genomes Project (Main) – Cancer Sequencing Consent Form",
					namePrefix: "GLM",
					templateVersion: "Version 2.3 dated 01.01.2017",
					cdrUniqueId : "GEL_MAN_V2_3"
					/* 1 */ ).addToQuestions(new Question(name: '(Taking part) I have read and understood the participant information sheet ‘for Personal or Nominated Consultees of patients with cancer (or suspected cancer)’ dated  /  /  (version   ). I have been able to ask questions and have these answered.',
							validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],
							defaultResponse: Response.ResponseValue.BLANK)
					/* 2 */ ).addToQuestions(new Question(name: '''(Samples) I agree to donate to the project: - a sample of blood;	- other samples, such as saliva, if needed; and	- samples already collected as part of my medical care. This includes samples of my tumour or bone marrow depending on the type of cancer I have.
																	My samples can be used for:	- collecting DNA for whole genome sequencing; and studying my blood to  nd out how the DNA is working. I understand that there might be new ways of doing this in the future.
																	My samples or DNA could be sent to approved organisations outside the UK for processing or analysis.''',
							validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],
							defaultResponse: Response.ResponseValue.BLANK)
					/* 3 */ ).addToQuestions(new Question(name: '(Data) I agree that the project can access and collect electronic copies of my past and future health records...',
							validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],
							defaultResponse: Response.ResponseValue.BLANK)
					/* 4 */ ).addToQuestions(new Question(name: '(My Result) I agree that: -tests can be run on my samples and health information to look for the cause of my cancer and may also help to  nd ways to manage my cancer; and -the results can be reported to my clinical team for them to discuss with me...',
							validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],
							defaultResponse: Response.ResponseValue.BLANK)
					/* 5 */).addToQuestions(new Question(optional: true,
							labelIfNotYes: "I do not want additional findings to be looked for and given to my clinical team.",
							name: '''Additional findings (optional): I understand the following.
										- I can choose if I want certain other conditions that might a ect me to be looked for in my samples (‘additional findings’).
										- These conditions are not connected to my cancer.
										- All the conditions can potentially be treated or prevented.
										- My results might also be important to other members of my family.
										- Even if my results seem to show that I don’t have one of the condi ons, I could s ll get it in the future.
										- We may add to or change which conditions we look for. This means I might get other results in the future.
										- I can change my mind about receiving additional findings at any  me.''',
							validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO],defaultResponse: Response.ResponseValue.NO)
					/* 6 */).addToQuestions(new Question(optional: true,
							labelIfNotYes: "I do not want reproductive additional results (carrier findings) to be looked for and fed back to my clinical team.",
							name: '''Carrier testing (optional): The next section is unlikely to be relevant to people who are not planning to have children in future. 
							 You can initial the box below and move to the next section.
							 I understand that:
								- I can decide to be tested to see if I ‘carry’ a risk of passing on serious genetic conditions to my future children or grandchildren;
								- these conditions may or may not be able to be cured, made less severe, or prevented using standard NHS treatment;
								- I may s ll have a child with one of the conditions, even if the result doesn’t identify the conditions in my genome data; and
								- you will regularly update the conditions looked for. This means I could get further reports about different conditions in the future.''',
							validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.NOT_RELEVANT],defaultResponse: Response.ResponseValue.NO)
			).save(failOnError: true)

		}



		if(ConsentFormTemplate.count()== 11)
		{
			new ConsentFormTemplate(
					name: "ORB General Consent Form",
					namePrefix: "GEN",
					templateVersion: "v3 March 2017 (03/03/2017)",
					cdrUniqueId : "ORB_GEN_V3"
			).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 2 dated 3rd March 2017). I have had the opportunity to ask questions and have had these answered satisfactorily.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree to give samples for research and/or allow samples already collected as part of my medical care to be used by the biobank.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for the biobank during the course of my hospital care.  I understand that I will be asked for permission each time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that my participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of my samples that have not already been used in research.  Withdrawing from the biobank will not affect my present and future medical care and legal rights in any way.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my health care records for research that uses my samples. I understand that the biobank will keep my information confidential. Information will only be passed on to researchers in a form that protects my identity.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return incidental findings", name: 'I understand that results from research tests on my samples might be medically important to me. I agree to my hospital consultant and GP being informed, and that research findings that are important for treating serious medical conditions I may have can be discussed with me.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO],defaultResponse: Response.ResponseValue.NO)
			).addToQuestions(new Question(name: 'I understand and agree that my samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, I would not profit financially.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any UK medical research that has research ethics committee approval and any research conducted outside the UK that has necessary country-specific approvals. I understand that future laboratory research may use new tests or techniques that are not yet known.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that relevant sections of my medical notes and data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my research records.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my samples may be used in genetic research aimed at understanding the genetic influences on disease and that the results of these investigations are unlikely to have any implications for me personally.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not contact", name: 'I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).save(failOnError: true)
		}



		if(ConsentFormTemplate.count() == 12) {
			new ConsentFormTemplate(
					name: "ORB Specific Programme Clinically Relevant Genomics - Oncology Consent Form for Adults",
					namePrefix: "CRA",
					templateVersion: "v3 March 2017 (03/03/2017)",
					cdrUniqueId : "ORB_CRA_V3"
					/* 1 */).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 2 dated 3rd March 2017). I have had the opportunity to ask questions and have had these answered satisfactorily.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 2 */).addToQuestions(new Question(name: 'I agree to give samples for research and/or allow samples already collected as part of my medical care to be used by the biobank.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 3 */).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for the biobank during the course of my hospital care.  I understand that I will be asked for permission each time.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 4 */).addToQuestions(new Question(name: 'I understand that my participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of my samples that have not already been used in research.  Withdrawing from the biobank will not affect my present and future medical care and legal rights in any way.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 5 */).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my health care records for research that uses my samples.  I understand that the biobank will keep my information confidential. Information will only be passed on to researchers in a form that protects my identity.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 6 */).addToQuestions(new Question(name: 'I understand and agree that my samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, I would not profit financially.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 7 */).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any UK medical research that has research ethics committee approval and any research conducted outside the UK that has necessary country-specific approvals. I understand that future laboratory research may use new tests or techniques that are not yet known.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 8 */).addToQuestions(new Question(name: 'I understand that relevant sections of my medical notes and data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my research records.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 9 */).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my samples may be used in genetic research aimed at understanding the genetic influences on disease.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 10 */).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return clinically relevant results to patient's clinician.", name: "Clinically relevant results: I agree that findings from genetic and other testing related to the reason I am currently undergoing investigations will be fed back to my clinician so that they may be used in decisions about my treatment.", validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO], defaultResponse: Response.ResponseValue.NO)
					/* 11 */).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return incidental findings", name: "Incidental findings: I understand and agree that I will be informed of any results of genetic analysis of my sample where they are NOT relevant to the condition being investigated, but are judged to be important for my/my family’s health care, and can be acted upon medically.", validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO], defaultResponse: Response.ResponseValue.NO)
					/* 12 */).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not contact", name: 'I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
			).save(failOnError: true)
		}



		if(ConsentFormTemplate.count() == 13) {
			new ConsentFormTemplate(
					name: "ORB Specific Programme Clinically Relevant Genomics - Oncology Parental Consent Form",
					namePrefix: "CRP",
					templateVersion: "Version 3 dated March 2017 (03/03/2017)",
					cdrUniqueId: "ORB_CRP_V3_0"
					/* 1 */).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 2 dated December 2016). I have had the opportunity to ask questions and have had these answered satisfactorily.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 2 */).addToQuestions(new Question(name: 'I agree to my child giving samples for research and/or allow samples already collected as part of my child’s medical care to be used by the biobank.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 3 */).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for the biobank during the course of my child’s hospital care. I understand that permission with be asked each time.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 4 */).addToQuestions(new Question(name: 'I understand that my child’s participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of their samples that have not already been used in research. Withdrawing from the biobank will not affect my child’s present and future medical care and legal rights in any way.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 5 */).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my child’s health care records for research that uses their samples. I understand that the biobank will keep my child’s information confidential. Information will only be passed on to researchers in a form that protects my child’s identity.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 6 */).addToQuestions(new Question(name: 'I understand and agree that my child’s samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, neither my child nor I would profit financially.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 7 */).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any UK medical research that has research ethics committee approval and any research conducted outside the UK that has necessary country-specific approvals. I understand that future laboratory research may use new tests or techniques that are not yet known.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 8 */).addToQuestions(new Question(name: 'I understand that relevant sections of my child’s medical notes and data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my child’s research records.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 9 */).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my child’s samples may be used in genetic research aimed at understanding the genetic influences on disease.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
					/* 10 */).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return clinically relevant results to patient's clinician.", name: "Clinically relevant results: I agree that findings from genetic and other testing related to the reason my child is currently undergoing investigations will be fed back to my child’s clinician so that they may be used in decisions about my child’s treatment.", validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO], defaultResponse: Response.ResponseValue.NO)
					/* 11 */).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return incidental findings to parents.", name: "Incidental findings: I understand and agree that I will be informed of any results of genetic analysis of my child’s sample where they are NOT relevant to the condition being investigated, but are judged to be important for my/my family’s health care, and can be acted upon medically.", validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO], defaultResponse: Response.ResponseValue.NO)
					/* 12 */).addToQuestions(new Question(optional: true, name: 'I agree to be contacted about ethically approved research studies for which my child may be suitable. I understand that agreeing to be contacted does not oblige my child to participate in any further studies.', validResponses: [Response.ResponseValue.YES, Response.ResponseValue.NO, Response.ResponseValue.BLANK, Response.ResponseValue.AMBIGUOUS], defaultResponse: Response.ResponseValue.BLANK)
			).save(failOnError: true)
		}



		if(ConsentFormTemplate.count()== 14)
		{
			new ConsentFormTemplate(
					name: "ORB Health Volunteers Consent Form",
					namePrefix: "OHV",
					templateVersion: "Version 1 dated March 2017 (03/03/2017)",
					cdrUniqueId : "ORB_OHV_V1"
			).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 1 dated 3rd March 2017). I have had the opportunity to ask questions and have had these answered satisfactorily.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree to give samples for research and allow the samples to be used by the biobank.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that further blood samples may be taken for the biobank. I understand that I will be asked for permission each time.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that my participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of my samples that have not already been used in research.  Withdrawing from the biobank will not affect my present and future legal rights in any way.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I understand that relevant sections of data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my research records. I understand that the biobank will keep my information confidential. Information will only be passed on to researchers in a form that protects my identity.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return incidental findings", name: 'I understand that results from research tests on my samples might be medically important to me.  I agree to provide contact details for my GP, and that my GP may be informed about research findings that are important for treating serious medical conditions, which may be discussed with me via my GP.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO],defaultResponse: Response.ResponseValue.NO)
			).addToQuestions(new Question(name: 'I understand and agree that my samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, I would not profit financially.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any UK medical research that has research ethics committee approval and any research conducted outside the UK that has necessary country-specific approvals. I understand that future laboratory research may use new tests or techniques that are not yet known.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'I agree that anonymised samples and data may be shared with other researchers, non-profit institutions or commercial organisations worldwide.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my samples may be used in genetic research aimed at understanding the genetic influences on disease and that the results of these investigations are unlikely to have any implications for me personally.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not contact", name: 'I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies.',validResponses: [Response.ResponseValue.YES,Response.ResponseValue.NO,Response.ResponseValue.BLANK,Response.ResponseValue.AMBIGUOUS],defaultResponse: Response.ResponseValue.BLANK)
			).save(failOnError: true)
		}



	}

    def destroy = {
    }


	private def addConsentFormForDevelopment(){
		def attachment = new Attachment(fileName: '1a.jpg', dateOfUpload: new Date([year: 2014, month: 2, date: 4]), attachmentType: Attachment.AttachmentType.IMAGE, content: []).save(flash: true)

		def template = new ConsentFormTemplate(
				id: 1,
				name: "ORB1",
				templateVersion: "1.1",
				namePrefix: "ABC",
		).addToQuestions(new Question(name: 'I read1...')
		).save()


		def consent = new ConsentForm(
				accessGUID: UUID.randomUUID().toString(),
				attachedFormImage: attachment,
				template: template,
				consentDate: new Date([year: 2014, month: 01, date: 01]),
				consentTakerName: "Edmin",
				formID: "ABC12345",
				formStatus: ConsentForm.FormStatus.NORMAL,
				consentStatus: ConsentForm.ConsentStatus.CONSENT_WITH_LABELS.CONSENT_WITH_LABELS
		).save();

		new Patient(
				givenName: "Patient1",
				familyName: "Clapton",
				dateOfBirth: new Date("30/03/1945"),
				hospitalNumber: "1002",
				nhsNumber: "1234567892",
				consents: []
		).addToConsents(consent).save()

	}
}
