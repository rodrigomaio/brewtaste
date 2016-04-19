(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .factory('TastingSearch', TastingSearch);

    TastingSearch.$inject = ['$resource'];

    function TastingSearch($resource) {
        var resourceUrl =  'api/_search/tastings/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
