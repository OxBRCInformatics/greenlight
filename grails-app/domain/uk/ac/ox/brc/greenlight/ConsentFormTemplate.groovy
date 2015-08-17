package uk.ac.ox.brc.greenlight

import org.codehaus.groovy.grails.plugins.orm.auditable.Stamp

@Stamp
class ConsentFormTemplate {

    String name
    String namePrefix
    String templateVersion
    List<Question> questions

	static auditable =  [ignore:['version','lastUpdated','lastUpdatedBy','createdBy','dateCreated']]

    static hasMany = [
            questions:Question
    ]
    static constraints = {

    }
    
    String toString()
    {
        return "${name} ${templateVersion}";
    }
}