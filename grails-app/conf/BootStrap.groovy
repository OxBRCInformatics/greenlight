import grails.converters.JSON
import org.codehaus.groovy.grails.web.converters.configuration.ChainedConverterConfiguration
import org.codehaus.groovy.grails.web.converters.configuration.ConvertersConfigurationHolder
import org.codehaus.groovy.grails.web.converters.configuration.DefaultConverterConfiguration
import org.springframework.jca.cci.CciOperationNotSupportedException
import org.springframework.web.context.support.WebApplicationContextUtils
import uk.ac.ox.brc.greenlight.ConsentForm
import uk.ac.ox.brc.greenlight.ConsentFormTemplate
import uk.ac.ox.brc.greenlight.Question
import uk.ac.ox.brc.greenlight.auth.AppRole
import uk.ac.ox.brc.greenlight.auth.AppUser
import uk.ac.ox.brc.greenlight.auth.UserRole

class BootStrap {

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

    def createFormTemplates(){

        if(ConsentFormTemplate.count()==0)
        {
            new ConsentFormTemplate(
                    name: "ORB General Consent Form",
                    namePrefix: "GEN",
                    templateVersion: "v1 October 2013"
            ).addToQuestions(new Question(optional: true, name: 'I have read and understood the information sheet for this study (Version 1 dated December 2013). I have had the opportunity to ask questions and have had these answered satisfactorily.')
            ).addToQuestions(new Question(name: 'I agree to give samples for research and/or allow samples already collected as part of my medical care to be used by the biobank.')
            ).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for the biobank during the course of my hospital care.  I understand that I will be asked for permission each time.')
            ).addToQuestions(new Question(name: 'I understand that my participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of my samples that have not already been used in research.  Withdrawing from the biobank will not affect my present and future medical care and legal rights in any way.')
            ).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my health care records for research that uses my samples.  I understand that the biobank will keep my information confidential. Information will only be passed on to researchers in a form that protects my identity.')
            ).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return incidental findings", name: 'I understand that results from research tests on my samples might be medically important to me.  I agree to my hospital consultant and GP being informed, and that research findings that are important for treating serious medical conditions I may have can be discussed with me.')
            ).addToQuestions(new Question(name: 'I understand and agree that my samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, I would not profit financially.  ')
            ).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any medical research that has research ethics committee approval. I understand that future laboratory research may use new tests or techniques that are not yet known.')
            ).addToQuestions(new Question(name: 'I understand that relevant sections of my medical notes and data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my research records.')
            ).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my samples may be used in genetic research aimed at understanding the genetic influences on disease and that the results of these investigations are unlikely to have any implications for me personally.')
            ).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not contact", name: 'I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies.')
            ).save(failOnError: true)
        }




        if(ConsentFormTemplate.count()==1)
        {
            new ConsentFormTemplate(
                    name: "ORB Specific Programme Clinically Relevant Genomics - Oncology Consent Form for Adults",
                    namePrefix: "CRA",
                    templateVersion: "v1 October 2013"
            ).addToQuestions(new Question(optional: true, name: 'I have read and understood the information sheet for this study (Version 1 dated October 2013). I have had the opportunity to ask questions and have had these answered satisfactorily.')
            ).addToQuestions(new Question(name: 'I agree to give samples for research and/or allow samples already collected as part of my medical care to be used by the biobank.')
            ).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for the biobank during the course of my hospital care.  I understand that I will be asked for permission each time.')
            ).addToQuestions(new Question(name: 'I understand that my participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of my samples that have not already been used in research.  Withdrawing from the biobank will not affect my present and future medical care and legal rights in any way.')
            ).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my health care records for research that uses my samples.  I understand that the biobank will keep my information confidential. Information will only be passed on to researchers in a form that protects my identity.')
            ).addToQuestions(new Question(name: 'I understand and agree that my samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, I would not profit financially.'  )
            ).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any medical research that has research ethics committee approval. I understand that future laboratory research may use new tests or techniques that are not yet known.')
            ).addToQuestions(new Question(name: 'I understand that relevant sections of my medical notes and data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my research records.')
            ).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my samples may be used in genetic research aimed at understanding the genetic influences on disease.')
            ).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return clinically relevant results", name: 'Clinically relevant results: I agree that findings from genetic and other testing related to the reason I am currently undergoing investigations will be fed back to my clinician so that they may be used in decisions about my treatment.')
            ).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not return incidental findings", name: 'Incidental findings: I understand and agree that I will be informed of any results of genetic analysis of my sample where they are NOT relevant to the condition being investigated, but are judged to be important for my/my family’s health care, and can be acted upon medically.')
            ).addToQuestions(new Question(optional: true, labelIfNotYes: "Do not contact", name: 'I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies.')
            ).save(failOnError: true)
        }


		if(ConsentFormTemplate.count()==2)
		{
			new ConsentFormTemplate(
					name: "100,000 Genomes Project – Cancer Sequencing Consent Form",
					namePrefix: "GEL",
					templateVersion: "Version 1.0 dated  25.08.2014"
			).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 1.0 dated 25.08.2014). I have had the opportunity to ask questions and have had these answered satisfactorily.')
			).addToQuestions(new Question(name: 'I understand that my participation is voluntary, that I am free to withdraw at any time without giving a reason, and that withdrawing will not affect my present or future medical care and legal rights in any way.')
			).addToQuestions(new Question(name: 'I agree to provide samples and/or allow samples already collected as part of my medical care to be used for this research.')
			).addToQuestions(new Question(name: 'I agree that further samples may be taken for this study during the course of my hospital care, if necessary.  I understand that I will be asked for permission each time.')
			).addToQuestions(new Question(name: 'I understand that my donated samples will be used to collect DNA from blood and from any cancer specimen for whole genome sequencing. This genetic information may be used in research aimed at understanding the genetic influences that contribute to cancer development and responses to treatment. Samples may also be used to study proteins and other structures.')
			).addToQuestions(new Question(name: 'I understand that my DNA sequence and anonymised clinical data will be deposited ultimately in a secure database held by Genomics England, where they can be accessed by approved investigators from the public or private sectors, for scientific or clinical purposes.')
			).addToQuestions(new Question(name: 'I consider these samples a gift to the Oxford University Hospitals NHS Trust and understand that I will not gain any direct personal or commercial benefit as a result of taking part in this project. I will also not gain any benefit from any other future research undertaken as a result of this gift.')
			).addToQuestions(new Question(name: 'I agree that research study staff can collect and store securely information from my health care records, now and in the future. I understand that the study researchers will keep my information confidential. Information will only be passed on in a form that protects my identity.')
			).addToQuestions(new Question(name: 'I agree to my GP being contacted and asked to share information about my medical history and to give access to medical records.')
			).addToQuestions(new Question(name: 'I understand that data collected during the study may be looked at by authorised individuals from Oxford University Hospitals NHS Trust, Genomics England Ltd, and other study monitors where it is relevant to my taking part in this research. I permit them to access my medical records.')
			).addToQuestions(new Question(name: 'I agree that if information is discovered from genetic and other testing related to the reason I am currently undergoing investigations, this will be fed back to my clinician and may be discussed with me regarding its use in decisions about my treatment. I understand that it is not yet known how long it would take to receive such results.')
			).addToQuestions(new Question(name: 'I understand and agree that I will NOT be informed of any results of genetic analysis of my sample where these are not relevant to the management of my current condition.')
			).addToQuestions(new Question(name: 'I agree to be contacted in future about this study and other ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate.')
			).save(failOnError: true)
		}

        if(ConsentFormTemplate.count()==3)
        {
            new ConsentFormTemplate(
                    name: "100,000 Genomes Project – Cancer Sequencing Consent Form",
                    namePrefix: "GEL",
                    templateVersion: "Version 2 dated 14.10.14"
            ).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 1.0 dated 25.08.2014). I have had the opportunity to ask questions and have had these answered satisfactorily.')
            ).addToQuestions(new Question(name: 'I understand that my participation is voluntary, that I am free to withdraw at any time without giving a reason, and that withdrawing will not affect my present or future medical care and legal rights in any way.')
            ).addToQuestions(new Question(name: 'I agree to provide samples and/or allow samples already collected as part of my medical care to be used for this research.')
            ).addToQuestions(new Question(name: 'I agree that further blood samples may be taken for this study during the course of my hospital care, if necessary. I understand that I will be asked for permission each time.')
            ).addToQuestions(new Question(name: 'I agree that the tissue and blood samples I give can be used, stored and distributed for use for research, including genetic testing and sequencing of the whole genome. This genetic information may be used in research aimed at understanding the genetic influences that contribute to cancer development and responses to treatment.')
            ).addToQuestions(new Question(name: 'I understand that my DNA sequence and anonymised clinical data will be deposited ultimately in a secure database held by Genomics England, where they can be accessed in anonymised form by approved investigators from the public or private sectors, for scientific or clinical purposes.')
            ).addToQuestions(new Question(name: 'I consider these samples a gift to the Oxford University Hospitals NHS Trust and understand that I will not gain any direct financial benefit as a result of taking part in this project. I will also not gain any financial benefit from any other future research undertaken as a result of this gift.')
            ).addToQuestions(new Question(name: 'I agree that research study staff can collect and store securely information from my health care records, now and in the future. I understand that the study researchers will keep my information confidential. Information will only be passed on in a form that protects my identity.')
            ).addToQuestions(new Question(name: 'I agree to my GP being contacted and asked to share information about my medical history and to give access to medical records.')
            ).addToQuestions(new Question(name: 'I understand that data collected during the study may be looked at by authorised individuals from Oxford University Hospitals NHS Trust, Genomics England, and other study monitors where it is relevant to my taking part in this research. I permit them to access my medical records.')
            ).addToQuestions(new Question(name: 'I agree that if information is discovered from genetic and other testing related to the reason I am currently undergoing investigations, this will be fed back to my clinician and may be discussed with me regarding its use in decisions about my treatment. I understand that it is not yet known how long it would take to receive such results.')
            ).addToQuestions(new Question(name: 'I understand and agree that I will NOT be informed of any results of genetic analysis of my sample where these are not relevant to the management of my current condition.')
            ).addToQuestions(new Question(name: 'I agree to be contacted in future about this study and other ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate.')
            ).save(failOnError: true)
        }

		if(ConsentFormTemplate.count()==4)
		{
			new ConsentFormTemplate(
					name: "Pre-2014 ORB consent form",
					namePrefix: "PRE",
					templateVersion: "Version 1.2 dated 3rd March 2009"
			).addToQuestions(new Question(name: 'I have read and understood the patient information sheet (green v1.2 dated 3rd March 2009). My questions have been answered satisfactorily. I know how to contact the research team.')
			).addToQuestions(new Question(name: 'I agree to give a sample of blood and/or other tissues for research.')
			).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for research during the course of my hospital care. I understand that I will be asked for permission each time.')
			).addToQuestions(new Question(name: 'I understand how the samples will be taken, that participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of my samples providing they have not already been used in research.')
			).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my health care records for research that uses my samples. I understand the biobank will keep my information confidential. Information will only be passed to researchers in an anonymous way that protects my identity.')
			).addToQuestions(new Question(name: 'I understand results from research tests on my samples might be medically important to me. I agree to my hospital consultant and GP being informed and that relevant experimental findings can be discussed with me.')
			).addToQuestions(new Question(name: 'I agree to gift blood samples taken for the purpose of the research study to the University of Oxford. If a commercial product were developed as a result of this study, I will not profit financially from such a product.')
			).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any medical research that has research ethics committee approval. I understand that future laboratory research may use new tests or techniques that are not yet known.')
			).addToQuestions(new Question(name: 'Consent for genetic research: I understand that my samples may be used in genetic research aimed at understanding the genetic influences on diseases and that the results of these investigations are unlikely to have any implications for me personally.')
			).addToQuestions(new Question(name: 'I understand that relevant sections of my medical notes and data collected by ORB, may be looked at by individuals from Oxford University, from regulatory authorities or from the NHS Trust, where it is relevant to my taking part in this research. I give permission for these individuals to have access to my records.')
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
    }

    def destroy = {
    }
}
