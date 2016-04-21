(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('tasting', {
            parent: 'entity',
            url: '/tasting',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'brewtasteApp.tasting.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tasting/tastings.html',
                    controller: 'TastingController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tasting');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('tasting-detail', {
            parent: 'entity',
            url: '/tasting/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'brewtasteApp.tasting.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/tasting/tasting-detail.html',
                    controller: 'TastingDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tasting');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Tasting', function($stateParams, Tasting) {
                    return Tasting.get({id : $stateParams.id});
                }]
            }
        })
        .state('tasting.new', {
            parent: 'tasting',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tasting/tasting-dialog.html',
                    controller: 'TastingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                date: null,
                                appearance: null,
                                aroma: null,
                                flavor: null,
                                mouthfeel: null,
                                finish: null,
                                generalImpression: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('tasting', null, { reload: true });
                }, function() {
                    $state.go('tasting');
                });
            }]
        })
        .state('tasting.edit', {
            parent: 'tasting',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tasting/tasting-dialog.html',
                    controller: 'TastingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Tasting', function(Tasting) {
                            return Tasting.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('tasting', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('tasting.delete', {
            parent: 'tasting',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/tasting/tasting-delete-dialog.html',
                    controller: 'TastingDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Tasting', function(Tasting) {
                            return Tasting.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('tasting', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
