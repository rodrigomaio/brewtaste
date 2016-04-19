(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .factory('BeerSearch', BeerSearch);

    BeerSearch.$inject = ['$resource'];

    function BeerSearch($resource) {
        var resourceUrl =  'api/_search/beers/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
