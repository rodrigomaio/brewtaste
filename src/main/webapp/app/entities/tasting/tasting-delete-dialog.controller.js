(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .controller('TastingDeleteController',TastingDeleteController);

    TastingDeleteController.$inject = ['$uibModalInstance', 'entity', 'Tasting'];

    function TastingDeleteController($uibModalInstance, entity, Tasting) {
        var vm = this;
        vm.tasting = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Tasting.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
