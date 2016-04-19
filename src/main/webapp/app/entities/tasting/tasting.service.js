(function() {
    'use strict';
    angular
        .module('brewtasteApp')
        .factory('Tasting', Tasting);

    Tasting.$inject = ['$resource', 'DateUtils'];

    function Tasting ($resource, DateUtils) {
        var resourceUrl =  'api/tastings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.date = DateUtils.convertDateTimeFromServer(data.date);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
