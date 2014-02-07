import uk.ac.ox.brc.greenlight.ConsentFormTemplate
import uk.ac.ox.brc.greenlight.Question
import uk.ac.ox.brc.greenlight.auth.AppRole
import uk.ac.ox.brc.greenlight.auth.AppUser
import uk.ac.ox.brc.greenlight.auth.UserRole

class BootStrap {

    def init = { servletContext ->


        environments {
            test {
                createRoles()
                createAdminUser("admin", "password", "support@example.com")
                createFormTemplates()
            }
            development {
                createRoles()
                createAdminUser("admin", "password", "support@example.com")
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
    }

    def createAdminUser(String username, String password, String email){
        if(!AppUser.findByUsername(username) ){
            def user = new AppUser(username: username, enabled: true, emailAddress: email, password: password).save(failOnError: true)
            UserRole.create user, AppRole.findByAuthority('ROLE_ADMIN')
        }
    }

    def createFormTemplates(){
        new ConsentFormTemplate(
                name: "ORB General Consent Form",
                namePrefix: "GEN",
                templateVersion: "v1 October 2013"
        ).addToQuestions(new Question(name: 'I have read and understood the information sheet for this study (Version 1 dated December 2013). I have had the opportunity to ask questions and have had these answered satisfactorily.')
        ).addToQuestions(new Question(name: 'I agree to give samples for research and/or allow samples already collected as part of my medical care to be used by the biobank.')
        ).addToQuestions(new Question(name: 'I agree that further blood and/or tissue samples may be taken for the biobank during the course of my hospital care.  I understand that I will be asked for permission each time.')
        ).addToQuestions(new Question(name: 'I understand that my participation is voluntary and that I am free at any time to withdraw my permission for the storage and distribution of any of my samples that have not already been used in research.  Withdrawing from the biobank will not affect my present and future medical care and legal rights in any way.')
        ).addToQuestions(new Question(name: 'I agree that biobank staff can collect and store information from my health care records for research that uses my samples.  I understand that the biobank will keep my information confidential. Information will only be passed on to researchers in a form that protects my identity.')
        ).addToQuestions(new Question(name: 'I understand that results from research tests on my samples might be medically important to me.  I agree to my hospital consultant and GP being informed, and that research findings that are important for treating serious medical conditions I may have can be discussed with me.')
        ).addToQuestions(new Question(name: 'I understand and agree that my samples will be considered a gift to the University of Oxford. If a commercial product were developed as a result of research in which my sample was used, I would not profit financially.  ')
        ).addToQuestions(new Question(name: 'I give permission for the biobank to store my samples and distribute them for use in any medical research that has research ethics committee approval. I understand that future laboratory research may use new tests or techniques that are not yet known.')
        ).addToQuestions(new Question(name: 'I understand that relevant sections of my medical notes and data collected by the biobank may be looked at by authorised individuals from The University of Oxford, NHS organisations, funding agencies and research governance monitors. I permit these individuals to access my research records.')
        ).addToQuestions(new Question(name: 'Genetic research: I understand and agree that my samples may be used in genetic research aimed at understanding the genetic influences on disease and that the results of these investigations are unlikely to have any implications for me personally.')
        ).addToQuestions(new Question(name: 'I agree to be contacted about ethically approved research studies for which I may be suitable. I understand that agreeing to be contacted does not oblige me to participate in any further studies.')
        ).save(failOnError: true)


        new ConsentFormTemplate(
                name: "ORB2",
                namePrefix: "ABC",
                templateVersion: "1.1"
        ).addToQuestions(new Question(name: 'I read2...')
        ).addToQuestions(new Question(name: 'I read2...')
        ).addToQuestions(new Question(name: 'I read2...')
        ).save(failOnError: true)

    }
    def destroy = {
    }
}
