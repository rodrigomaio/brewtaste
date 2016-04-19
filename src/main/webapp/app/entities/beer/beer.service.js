(function() {
    'use strict';
    angular
        .module('brewtasteApp')
        .factory('Beer', Beer);

    Beer.$inject = ['$resource'];

    function Beer ($resource) {
        var resourceUrl =  'api/beers/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
