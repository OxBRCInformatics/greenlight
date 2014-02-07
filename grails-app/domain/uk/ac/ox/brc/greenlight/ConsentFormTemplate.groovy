package uk.ac.ox.brc.greenlight

class ConsentFormTemplate {

    String name
    String namePrefix
    String templateVersion
    List<Question> questions

    static auditable = true

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