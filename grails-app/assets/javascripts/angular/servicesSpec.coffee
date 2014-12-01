#= require  main
#= require  services

describe "app level", ()->
  beforeEach module("greenlight.services")

  describe "service level" , () ->
    service = null

    beforeEach inject ($injector)->
      service = $injector.get 'greenlight.services.attachment'

    it "will return list of available uris", ->
      expect(service.controllerName).toEqual("attachment")
      service.get(1,3444)
      debugger