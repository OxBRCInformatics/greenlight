#= require angular-resource/angular-resource.js


app = angular.module("greenlight.services",['ngResource'])


#calls API endpoints on the server
app.service("greenlight.services.resources" ,["$resource",($resource) ->

    getResource : (controller,action,id)->
      $resource "/:controller/:action/:id.json", {controller: controller || '', action:action || '' ,id: id || ''}
])


app.factory("greenlight.services.attachment" ,["greenlight.services.resources",(resource)->
  {
    get: (id) ->
      resource.getResource("attachment","show",id).get()

    list: () ->
      resource.getResource("attachment","list").query()

    delete : (id)->
      resource.getResource("attachment","delete",id).get()

#      OR we can have more complicated and also check for errors
#      data = resource.getResource("attachment","show",id).get({}
#        ,() ->
#          console.log data
#        ,(e)->
#          data = {errors:true}
#          console.log e
#      )
#      data
  }

])