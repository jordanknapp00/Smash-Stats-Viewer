# Smash Stats Viewer

A simple program to view .sel statistics files generated by my [Smash Character Picker](https://github.com/jordanknapp00/Smash-Character-Picker/). As you use that program, you can save the results of battles to keep track of players' win/loss ratios. The statistics are always stored in a file called `smash stats.sel`, and the Smash Character Picker does not have the capability to load stats files of other names. The reason for this is that modifying many different stats files under many different tier lists can cause problems. Enter the stats viewer, a read-only implementation for stats files.

## Using the program

Simply launch the program and load an `.sel` file. You can use the "Load" button or simply drag a file over the window. If the file is valid (i.e. it contains a `HashMap` object which corresponds to a valid set of statistics), you will be able to view the statistics using the "Look up stats" button. From here, the functionality is almost the same as what is present in the Smash Character Picker, just without the ability to modify stats.

### Lookup

Individual fighters within the system can be looked up, and their stats for each player are displayed. For example, if you want to see each player's winrate as Link, and Link's overall winrate, simply enter "Link" into the "Lookup" box, and either press enter or hit the "Look up" button. The relevant statistics are printed to the screen.

### Sort by

You can also sort all fighters in the system by various metrics, including a fighter's overall winrate across all players, a fighter's winrate with an individual player, and the total number of battles that a fighter has appeared in. Finally, you can see each *player's* overall winrate as well.
