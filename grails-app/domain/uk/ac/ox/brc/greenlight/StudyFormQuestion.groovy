package uk.ac.ox.brc.greenlight

class StudyFormQuestion {

    String name

    static belongsTo = [
            studyForm:StudyForm]

    static constraints = {
    }

    enum QuestionType {
        UNKOWN("Unknown"), YES_NO("Yes No"),DESCRIPTION("Description");
        final String value;
        QuestionType(String value) { this.value = value; }
        String toString() { value; }
        String getKey() { name(); }
    }
}
