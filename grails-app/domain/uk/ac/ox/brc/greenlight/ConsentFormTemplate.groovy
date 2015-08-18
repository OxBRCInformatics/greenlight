package uk.ac.ox.brc.greenlight

class ConsentFormTemplate {

	String cdrUniqueId
    String name
    String namePrefix
    String templateVersion
    List<Question> questions

    static auditable = true

    static hasMany = [
            questions:Question
    ]
    static constraints = {

		cdrUniqueId nullable:true, unique: true
    }
    
    String toString()
    {
        return "${name} ${templateVersion}";
    }
}