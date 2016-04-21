(function() {
    'use strict';

    angular
        .module('brewtasteApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('beer', {
            parent: 'entity',
            url: '/beer?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'brewtasteApp.beer.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/beer/beers.html',
                    controller: 'BeerController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('beer');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('beer-detail', {
            parent: 'entity',
            url: '/beer/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'brewtasteApp.beer.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/beer/beer-detail.html',
                    controller: 'BeerDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('beer');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Beer', function($stateParams, Beer) {
                    return Beer.get({id : $stateParams.id});
                }]
            }
        })
        .state('beer.new', {
            parent: 'beer',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/beer/beer-dialog.html',
                    controller: 'BeerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                rateBeerId: null,
                                abv: null,
                                overallRating: null,
                                styleRating: null,
                                style: null,
                                country: null,
                                brewery: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('beer', null, { reload: true });
                }, function() {
                    $state.go('beer');
                });
            }]
        })
        .state('beer.edit', {
            parent: 'beer',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/beer/beer-dialog.html',
                    controller: 'BeerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Beer', function(Beer) {
                            return Beer.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('beer', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('beer.delete', {
            parent: 'beer',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/beer/beer-delete-dialog.html',
                    controller: 'BeerDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Beer', function(Beer) {
                            return Beer.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('beer', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
