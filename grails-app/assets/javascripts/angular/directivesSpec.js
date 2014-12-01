describe('Directive: albums', function () {
	var element, scope, document;


	beforeEach(module('consentQuestionTemplate'));

	beforeEach(inject(function ($rootScope, $compile, $document) {
		scope = $rootScope.$new()
		document = $document
		var elementString = "<div><template-questions consent ='consent' ></template-questions></div>"

		element = $compile(elementString)(scope);

	}));

	it("consentQuestion will load all responses of a consent", function () {

		var validValues0 = [
			{name: "YES",	value: "5"},
			{name: "NO", 	value: "6"},
			{name: "BLANK", value: "8"}
		]

		var GelForm =
		{id: "1", name: "GEL Form", prefix: "GEL", questions: [
			{id: "1", defaultValue: "8", title: "Q-GEL1", validValues: validValues0},
			{id: "2", defaultValue: "8", title: "Q-GEL2", validValues: validValues0},
			{id: "3", defaultValue: "8", title: "Q-GEL3", validValues: validValues0},
			{id: "4", defaultValue: "8", title: "Q-GEL4", validValues: validValues0}
		]}


		scope.consent = []
		scope.consent.consentFormTemplate = GelForm
		scope.consent.responses = [
			{id: "1", answer: validValues0[0], question: GelForm.questions[0]},
			{id: "2", answer: validValues0[1], question: GelForm.questions[1]},
			{id: "3", answer: validValues0[1], question: GelForm.questions[2]},
			{id: "4", answer: validValues0[0], question: GelForm.questions[3]}
		]
		scope.$digest();


		var questionsList = element.find('li');
		expect(questionsList.length).toEqual(4);


		var questionsText =  element.find("span.questionTitle");
		expect(questionsText[0].innerHTML).toEqual(GelForm.questions[0].title)

		//There should be for select elements based on 4 questions
		var questionSelect = element.find('select')
		expect(questionSelect.length).toEqual(4)

		//each question has validValues as its option
		var Options = angular.element(questionSelect[0]).find('option')
		expect(Options.length).toEqual(GelForm.questions[0].validValues.length + 1) //1 for --select-- option

		//all Options have correct option name
		expect(angular.element(Options[0]).text()).toEqual("--select--")
		expect(angular.element(Options[1]).text()).toEqual(GelForm.questions[0].validValues[0].name)
		expect(angular.element(Options[2]).text()).toEqual(GelForm.questions[0].validValues[1].name)

		//all Options have correct option value
		expect(angular.element(Options[1]).val()).toEqual(GelForm.questions[0].validValues[0].value)
		expect(angular.element(Options[2]).val()).toEqual(GelForm.questions[0].validValues[1].value)
		expect(angular.element(Options[3]).val()).toEqual(GelForm.questions[0].validValues[2].value)
	})

})