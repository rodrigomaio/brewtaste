(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .controller('BeerDeleteController',BeerDeleteController);

    BeerDeleteController.$inject = ['$uibModalInstance', 'entity', 'Beer'];

    function BeerDeleteController($uibModalInstance, entity, Beer) {
        var vm = this;
        vm.beer = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Beer.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
