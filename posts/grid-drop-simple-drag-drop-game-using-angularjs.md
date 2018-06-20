---
authors:
- Kenneth Hegeland
categories:
date: 2016-02-17T11:22:03.000Z
title: "Grid Drop -- A Simple Drag and Drop Game Using AngularJS"
id: 5a267e57dd54250018d6b5f8
image: 
---

In this article, we will learn about AngularJS directives and the use of HTML5 drag and drop, to that end I have created a simple game and added it to my GitHub account, it can be found [here](https://github.com/hegek87/gridDrop). The github project uses JHipster to generate an app, and cloning it will get some extra stuff that is unnecessary, it can be used to explore various steps (via the branches), the final solution is also available in this [codepen](http://codepen.io/khegeland/pen/MywMwa?editors=1000). The basic idea is to have a square grid of colors as well as a single square next to it. Each color is worth a different amount of points and the goal is to drag the individual square into the grid. When the user drags the single square away, a new square will be randomly generated. Placing two squares on the grid adjacent to each other will cause both to disappear and the user to gain points equal to each destroyed square. The game continues until the grid fills up and the user has no where to drop a square. We will start by creating directives to allow the squares to be dragged and dropped (although nothing will happen when dropping yet). First, we must briefly discuss what an AngularJS directive is, from the [directive developer guide](https://docs.angularjs.org/guide/directive), we have the following quote

At a high level, directives are markers on a DOM element (such as an attribute, element name, comment or CSS class) that tell AngularJS’s HTML compiler [($compile)](https://docs.angularjs.org/api/ng/service/$compile) to attach a specified behavior to that DOM element (e.g. via event listeners), or even to transform the DOM element and its children.

We will be using directives which use both methods mentioned (add behavior, and transform DOM elements).

## Creating basic directives for dragging and dropping

The first step is to create something simple which will work properly, but will not be very useful, and then we will build on top of it to achieve our goal. We would like to have an attribute directives which allow us to make the element it is an attribute of draggable

```language-javascript
angular.module('griddropApp')
    .directive('dragTarget', function() {
        return {
            restrict: 'A',
            replace: true,
            link: function(scope, el, attrs, ctrl) {
                angular.element(el).attr("draggable", "true");

                el.bind('dragstart', function(e) {
                    e.originalEvent.dataTransfer.effectAllowed = 'move';

                    // This is just required for Firefox
                    e.originalEvent.dataTransfer.setData('text/plain', 'stop');

                    angular.element(e.target).addClass('dragged');
                });

                el.bind('dragend', function(e) {
                    angular.element(e.target).removeClass('dragged');
                })
            }
        }
    });
```

Drag directive
The important part of this directive is where we set the draggable attribute to true. We bind the drag start and end events to just add and remove a CSS class. The drop directive currently just handles events.

```language-javascript
angular.module('griddropApp')
    .directive('dropTarget', function() {
        return {
            restrict: 'A',
            link: function(scope, el, attrs, ctrl) {
                el.bind('drop', function(e) {
                    if(e.preventDefault) {
                        e.preventDefault();
                    }
                    if(e.stopPropagation) {
                        e.stopPropagation();
                    }
                    angular.element(e.currentTarget).removeClass('hover');
                });

                el.bind('dragover', function(e) {
                    if(e.preventDefault) {
                        e.preventDefault();
                    }

                    return false;
                });

                el.bind('dragenter', function(e) {
                    angular.element(e.currentTarget).addClass('hover');
                });

                el.bind('dragleave', function(e) {
                    angular.element(e.currentTarget).removeClass('hover');
                });
            }
        }
    });
```

We stop the default behavior in many of the bound events. This gives us complete control of what will happen (so far, nothing fancy). Now, we can test out these directives and see what happens when they are added to an element. In order to do this, let’s create our square now, which will be named gridDropSquare.

## Creating Our Grid Drop Square

This directive will be an element rather than an attribute, and it will use our drag and drop directives defined above.

```language-javascript
angular.module('griddropApp')
    .directive('gridDropSquare', function() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                size: '=',
                content: '='
            },
            link: function(scope, el, attrs, ctrl) {},
            templateUrl: 'scripts/components/dragAndDropGrid/compartment.html'
        }
    });
```

The size scope variable is used to determine what proportion of the containing element should be used. A size of 1 corresponds to 1 square uses the entire space, and in general, a size of n corresponds to n squares required to fill the space. The view is provided via a templateUrl and looks as follows:

```language-html
<div class="grid-size-{{ size }} grid-cell">

<div class="grid-square-content">

<div class="grid-table">

<div class="grid-table-cell">
                {{ content.score }}
</div>

</div>

</div>

</div>

```

The CSS classes used allow us to create a responsive grid of squares with centered text, lots of detail about this CSS can be found in [this](http://stackoverflow.com/a/20457076/3239052) Stack Overflow answer. Now we will add a square to the main page and be able to see what has been done so far. If you have cloned the git repo you can access everything we have done so far by going to the branch ‘story-createGridOfDraggableSquares’. This project was generated with [JHipster](http://jhipster.github.io/) and can be run using only the command ‘mvn spring-boot:run’. Now that we can drag our squares, we would like to make something useful actually happen (like allowing us to drop a square on a new position).

## Communicating Between Directives

Two things are done to enable this, first, we add the following service, which allows us to share information:

```language-javascript
angular.module('griddropApp')
    .factory('DragAndDropHelper', function() {
        var content;

        return {
            setContent: function(content) {
                this.content = content;
            },

            getContent: function() {
                return this.content;
            }
        }
    });
```

and then we require the drag and drop directives to have access to our square’s controller. Requiring a controller is as simple as adding the require directive property along with what directive we require the controller to. Both the Drag, and Drop directives have been updated to add the following line

```language-javascript
angular.module('griddropApp')
    .directive('dropTarget', ['DragAndDropHelper', function(DragAndDropHelper) {
        return {
            restrict: 'A',
            require: 'gridDropSquare',
            link: function(scope, el, attrs, ctrl) {
                // same as before
            }
        }
    }]);
```

and we also injected the new service into the directive. Now we have everything set up to allow us to communicate the content between different squares, the basic flow is as follows:

When the user begins the drag, get the content of the current square and store it in the DragAndDropHelper
```language-javascript
angular.module('griddropApp')
    .directive('dragTarget', ['DragAndDropHelper', function(DragAndDropHelper) {
        return {
            restrict: 'A',
            replace: true,
            require: 'gridDropSquare',
            link: function(scope, el, attrs, ctrl) {
                el.bind('dragstart', function(e) {
                    e.originalEvent.dataTransfer.effectAllowed = 'move';

                    // We don't use the data transfer to move data, instead we communicate
                    // through required controllers. However, firefox requires data to be set
                    e.originalEvent.dataTransfer.setData('text/plain', 'stop');

                    DragAndDropHelper.setContent(ctrl.getContent());
                    angular.element(e.target).addClass('dragged');
                });
            }
        }
    }]);
```

Set the DragAndDropHelper contents

1. When the user drops the element, set the content of the destination square to be equal to the content stored in the DragAndDropHelper
2. Refresh the directives view.

```language-javascript
angular.module('griddropApp')
    .directive('dropTarget', ['DragAndDropHelper', function(DragAndDropHelper) {
        return {
            restrict: 'A',
            require: 'gridDropSquare',
            link: function(scope, el, attrs, ctrl) {
                el.bind('drop', function(e) {
                    if(e.preventDefault) {
                        e.preventDefault();
                    }
                    if(e.stopPropagation) {
                        e.stopPropagation();
                    }
                    ctrl.setContent(DragAndDropHelper.getContent());
                    angular.element(e.currentTarget).removeClass('hover');
                    scope.$apply();
                });
            }
        }
    }]);
```
Steps 1 and 2

The second step, calling `scope.$apply()` at the end of the drop event is very important. Since the event takes place outside of the AngularJS world, the scope variables are updated, but the view won’t know about these changes. Calling $apply starts a digest cycle and causes the view to be updated.

Now we have working drag and drop in our grid, but it’s a little boring looking, let’s add some color. When a drag and drop is made, the entire content object being stored in our square is moved, so we can add details to the view based on the content object and see the changes reflected in a drag and drop.  We add a background-color to the square view, and modify the text to be a header with a text shadow:

```language-html

<div ng-style="{ 'background-color' : content.color }" class="grid-size-{{ size }} grid-cell">

<div class="grid-square-content">

<div class="grid-table">

<h2 class="grid-table-cell grid-drop-text-border">
                {{ content.score }}
</h2>

</div>

</div>

</div>

```

To generate the colors and numbers, we add a method to our main controller to generate 9 content objects, and then render each of them in our main view.

The current progress is available on the branch story-addServiceToShareContent.

## Adding Logic to Grid Drop

This game is starting to match what was described at the beginning, we can drag and drop randomly generated squares, but we still need to add the main logic which allows us to play the game properly. Our main view should have two sections, a grid of squares, and a single square (which we will refer to as the palette) where we grab new squares from. We also initialize the palette to be a random color and score value. In order to randomly generate a new color and score for the palette, we need to differentiate between the palette and the grid, we do this by adding a new scope variable on our square directive, called isPalette. Finally, we add a check in our dragend event to see if we ended dragging the palette square, and if we did, we get a new random piece of content.

```language-javascript
angular.module('griddropApp')
    .directive('dragTarget', ['DragAndDropHelper', 'RandomContent', function(DragAndDropHelper, RandomContent) {
        return {
            restrict: 'A',
            replace: true,
            require: 'gridDropSquare',
            link: function(scope, el, attrs, ctrl) {
                el.bind('dragend', function(e) {
                    if(ctrl.isPalette()) {
                        ctrl.setContent(RandomContent.getRandomContent())
                    }
                    angular.element(e.target).removeClass('dragged');
                    scope.$apply();
                })
            }
        }
    }]);
```

Next we want to disable drag ability in the grid and only allow the user to drag from the palette to the grid. It would seem that removing the drag-target directive from the grid squares would work, however if we do, the default behavior is used, and if we select the text, it becomes draggable. So removing the drag-target directive does not make it undraggable. Instead, we pass in a boolean and use that to set the allowed drag effects:

```language-javascript
angular.module('griddropApp')
    .directive('dragTarget', ['DragAndDropHelper', 'RandomContent', function(DragAndDropHelper, RandomContent) {
        return {
            restrict: 'A',
            replace: true,
            require: 'gridDropSquare',
            link: function(scope, el, attrs, ctrl) {
                angular.element(el).attr("draggable", attrs['dragTarget']);

                el.bind('dragstart', function(e) {
                    e.originalEvent.dataTransfer.effectAllowed = attrs['dragTarget'] === 'true' ? 'move' : 'none';

                    // everything else as before
                });
            }
        }
    }]);
```

This will allow us to design our main view

```language-html

<div ng-cloak>

<div class="row">

<div class="col-md-6">
            <grid-drop-square size="3" is-palette="true" content="initial" drag-target="true"></grid-drop-square>
</div>

<div class="grid col-md-6">

<div ng-repeat="row in contents">
                <grid-drop-square ng-repeat="content in row" size="3" content="content" drop-target drag-target="false">

                </grid-drop-square>
</div>

</div>

</div>

</div>

```

and have the grid contents not draggable, but the palette square is draggable. Next we need to be able to specify which position in the grid a square will occupy, so we update our gridDropSquare directive to add two new scope variables (representing the x and y positions in the grid):

```language-javascript
angular.module('griddropApp')
    .directive('gridDropSquare', function() {
        return {
            restrict: 'E',
            replace: true,
            scope: {
                size: '=',
                content: '=',
                isPalette: '=',
                gridDropX: '=',
                gridDropY: '='
            }
            // everything else as before
        }
    });
```

In order for changes to a square’s contents to be recognized in the main controller, we must modify our main view slightly:

```language-html

<div ng-cloak>

<div class="row">
        <span>
            <label translate="main.score"></label>
            {{ totalScore }}
        </span>
</div>

<div class="row">

<div class="col-md-6">
            <grid-drop-square size="3" is-palette="true" content="initial" drag-target="true"></grid-drop-square>
</div>

<div class="col-md-6">

<div ng-repeat="row in contents track by $index" ng-init="x=$index">

<div ng-repeat="col in row track by $index" ng-init="y=$index">
                    <grid-drop-square size="3" grid-drop-x="x" grid-drop-y="y" content="contents[x][y]" drop-target drag-target="false">

                    </grid-drop-square>
</div>

</div>

</div>

</div>

</div>

```

Now our grid squares are aware of the location in the grid they occupy.

Finally we need to add the ability to check whether the newly placed square has any others adjacent to it, and if so, clear them and add the value to the current score. Our collection of content is created in our main controller, and the gridDropSquare only knows about a single square. To update the collection, we use [$emit](http://stackoverflow.com/a/26752415/3239052) to send an event upwards to the main controller listener (since the main scope is a parent scope to our gridDropSquare) which contains the position that the square was dropped on.

```language-javascript
angular.module('griddropApp')
    .directive('dropTarget', ['DragAndDropHelper', function(DragAndDropHelper) {
        return {
            restrict: 'A',
            require: 'gridDropSquare',
            link: function(scope, el, attrs, ctrl) {
                el.bind('drop', function(e) {
                    if(e.preventDefault) {
                        e.preventDefault();
                    }
                    if(e.stopPropagation) {
                        e.stopPropagation();
                    }
                    ctrl.setContent(DragAndDropHelper.getContent());
                    angular.element(e.currentTarget).removeClass('hover');
                    scope.$emit('grid-drop-change', {
                        dropPositionX: ctrl.getGridDropX(),
                        dropPositionY: ctrl.getGridDropY()
                    });
                    scope.$apply();
                });
            }
        }
    }]);
```

The main controller listens for the event and checks for adjacent squares, then adds the value to the total score and clears the necessary squares. The method to clear squares is to simply check the 7 squares around the one we just dropped and clear them if they have a matching color. If any match was found, we clear the dropped square (the code for clearing squares can be found on the master branch).

```language-javascript
'use strict';

angular.module('griddropApp')
    .controller('MainController', ['$scope', 'Principal', 'RandomContent', function ($scope, Principal, RandomContent) {
        ...

        $scope.$on('grid-drop-change', function(event, position) {
            $scope.clearMatches($scope.contents, position.dropPositionX, position.dropPositionY);
        });

        ...
    }]);
```

## Ending the Game

We finally want to be able to end the game after some condition is met; As mentioned earlier, the user will lose when he runs out of spaces to drag squares onto (that is, no grey squares exists). We do this by checking for at least one grey square after dropping a square on the grid. If no square has found, we disable drag functionality on the palette and enable a button which allows the user to restart the game.

With this method of drag and drop, we aren’t actually dragging the dom element, we are instead dragging a javascript object during the drag. This method allows us to easily customize the behavior in many ways. To illustrate, we update our directive to set the class used in our template to be based on a javascript string. With this change, we can easily change what shape is being used. The final product, available on the master branch, allows the user to change the elements between squares and circles via a dropdown menu, it would not be difficult to add new shapes. AngularJS allows a lot of power and flexibility, it can help organize your application into components that can be easily reused. When working with AngularJS, directives should be embraced for this exact reason.
