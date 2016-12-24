# DevKit
A set of toys for the use of developers.

# About
The goal of DevKit is to provide Bukkit developers with tools necessary to test plugins they are creating. While this plugin does not aim
to be a library in itself, it can be built upon and extended to add more functionality to some of the features in DevKit.

## Toys
What good are tools if they aren't fun to use? I've called them toys for this reason. Currently, I plan to add the following to DevKit:
* DevWand (see below)
* Commands for teleporter creation/manipulation

### DevWand
The DevWand is a tool that allows you to select a set of entities and activate various ... "actions"... There are a variety of actions to use, including
(but not limited to):
* Lightning
* Teleport
* Explode
* Launch

There is also support to add your actions, if you so wish.

#### How does it work?
By default, wands are in the 'off' state. By using the command `/devwand on`, you can turn it on and use it (providing you have permission of course. But you're most likely
a server dev/op, so you'll have permission, right?). To select entities, you'll need to set a selector (or change the selection mode). Afterwards, you can simply left-click while holding a
stick in your main hand to select entities. After you've added actions to your wand, right click while holding a stick in either hand and the wand will activate.

Multiple actions can be added at once, and they will all be chained together. There is even  a 'delay' action to make the rest of the actions happen later.

More information will be available on this project's [wiki](///www.github.com/MalignantShadow/Bukkit-DevKit/wiki), so be sure to head over there.

# Dependencies
* ShadowAPI Bukkit plugin