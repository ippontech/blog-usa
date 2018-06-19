---
authors:
- Justin Risch
categories:
- 
date: 2015-01-09T13:50:11.000Z
title: "Swing like you're on the Web."
id: 5a267e57dd54250018d6b5da
image: 
---

As a Java Developer, I have the benefit of knowing that my language of choice will always work, whether I need to make a desktop application, a web application, or a mobile app. This means the projects I tackle, as a consultant, can vary from a standard form submission web application to a Java Swing application with hundreds of views. In hopping back and forth between the two subjects, there is a trend that becomes apparent– when programmers don’t need to worry about load times, the code gets sloppy.

Suppose we have a website with 50 views (pages). This means we probably have about 54 HTML files– a layout, menu, header, footer, and 50 distinct bodies. We design websites to have this beautiful quality of compartmentalization where only the parts that need changing are loaded– but what of Swing Applications? After all, an application switching tabs has a thousandth of the load time of a web page; why would we worry about reloading already visible components? This practice isn’t to save the user time. You’re saving your company and yourself time by reducing the amount of code initially written, and the number of edits needed to maintain that code.

Suppose you wanted a menu bar on every view of your application, so that no matter the window that’s open, you can access that functionality. There are three ways to accomplish this goal— the good, the bad, and the ugly. Let’s start with the last and work our way back.

## The Ugly

With a little copy paste, it wouldn’t be hard to put that button to each JFrame class in the application so it appears in the same location, with the same ActionListener behavior. Within my circle of friends, we have a saying– Copy Pasta makes Spaghetti Code. Should you want to change its behavior, fix a bug perhaps, or change the word “back” on it to the image of a backward pointing arrow, you would have 50 sections of code to change in this example– this is the ugliest way to maintain your code, and I guarantee you that you will fix those buttons for weeks as you discover that some were not properly updated when the bug reports come in.

**Ugly Example:**
 You would have this on every view of your application! Imagine adding a new menu option.
 For just saving and exiting, it would mean 21 lines of additional code per view, cluttering your overall space and making it difficult to maintain.

```language-java
menuBar = new JMenuBar();
JMenu menu = new JMenu(“File”);
JMenuItem item = new JMenuItem(“Save”);
item.addActionListener( new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
saveInformation();
}
} );
menu.add(item);
item = new JMenuItem(“Exit”);
item.addActionListener( new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
System.exit(0);
}
} );
menu.add(item);
menuBar.add(menu);
contentPane.add(menuBar, BorderLayout.NORTH);
```

## The Bad

Though it’s far better than the above example, the “bad” way of doing this is to define a MenuBar object, then place it manually on each view. This means that you will still have to manually add the menu bar to each view of your application. Not so bad right? Changing the behavior would mean changing one location, and we could easily add or remove options from the menu in one location. Well sure, but what if your UI needed retouching? Suppose you decide later that you want to anchor the menu to the bottom of the window rather than the top– you still have to fix it on every single view. As well, if you wanted to rename your MyMenuBar into something more specific, you would have to edit that class name for every view.

Bad Example:

1. First, define a MyMenuBar object.

```language-java
public class MyMenuBar extends JMenuBar {
public MyMenuBar(){
//Menu bar at the top.
//Options under File
JMenu menu = new JMenu(“File”);
//SAVE
JMenuItem item = new JMenuItem(“Save”);
item.addActionListener( new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
JDialog JD = new JDialog();
JD.setBounds(200, 200, 200, 10);
JD.setTitle(“Saved!”);
JD.setVisible(true);
}
} );
menu.add(item);
//EXIT
item = new JMenuItem(“Exit”);
item.addActionListener( new ActionListener() {
@Override
public void actionPerformed(ActionEvent e) {
System.exit(0);
}
} );
menu.add(item);
this.add(menu);
}
}
```

Then, on each view you would need to put these lines of code:
```language-java
MyMenuBar menuBar = new MyMenuBar();
contentPane.add(menuBar, BorderLayout.NORTH);
```

Which isn’t too cumbersome, but if you want to change the layout used, the location of the bar, or the name of the class associated with it, you’re going to run into a lot of maintenance issues.

## The Good

This leaves the “good” way of doing things. First, define a Layout class which extends JFrame. Divide it up into tiles like you would a web application, and define anything that stays on every view of the Swing application here. This means we would only have to add the MyMenuBar object here, once. If it needed to be moved, it would mean changing at most 2 lines of code (X and Y coordinates if you’re using a GridBagLayout, my personal favorite). If one page shouldn’t have it there, you can make it invisible while that view is open, since making components invisible in a local application is far safer than trusting an online user to not right-click and view the source.

Ultimately, the goals of this methodology is the same as many of the tenets of OOP and Java alike– reduce the amount of code, the difficulty of maintenance, and the complexity of the application. Simplicity is a noble goal, and I hope more people try to keep this in mind when making their applications.

**Good Example:**

- Define the MyMenuBar object, as seen in the “bad” example.
2. Define a template class that extends JFrame, and add the menubar to the top. While we’re at it, add an InternalPanel.

```language-java
public class Template extends JFrame {
private JPanel contentPane;
private JMenuBar menuBar;
protected JPanel InternalPanel;
public static void main(String[] args) {
EventQueue.invokeLater(new Runnable() {
public void run() {
try {
Template frame = new Template();
frame.setVisible(true);
} catch (Exception e) {
e.printStackTrace();
}
}
});
}
public Template() {
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setBounds(100, 100, 450, 300);
contentPane = new JPanel();
contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
contentPane.setLayout(new BorderLayout(0, 0));
/* This is where we define persistent/shared components.
* In this application, it will consist of a menu bar at the top and
* an InternalPanel located in the “west”. */
setContentPane(contentPane);
InternalPanel = new JPanel();
contentPane.add(InternalPanel, BorderLayout.WEST);
MyMenuBar menuBar = new MyMenuBar();
contentPane.add(menuBar, BorderLayout.NORTH);
}
}
```

- Add this method to your code.

```language-java
private void setInternalPanel(Component c){
InternalPanel.removeAll();
InternalPanel.add(c);
InternalPanel.revalidate();
InternalPanel.repaint();
}
```

Now we’re all set up! Anytime you want to change your view, pass the new view to the setInternalPanel method, and it will update your application without reloading the menu bar (or any other components you have in the template, such as a footer or company logo). Since JPanels are components, you can actually pass it an entire panel with it’s own layout and subcomponents. In the screenshots below, I have added a “View” menu, which allows you to change between the pages without reloading the footnote or menu.Screenshots. If you’d prefer a more in depth example than the above code, the code that corresponds to the screenshots below can be found on [Github](https://github.com/JustinRisch/TemplateSwing).

Using the menu to switch between views…
![Using the menu to switch between views](https://i.imgur.com/p8REQzK.png)

Page 2 is complicated. It contains a BorderLayout panel, which has a JLabel in the north, a Table in the center, and another JPanel in the south. The Southern panel contains a JLabel in the west, and a text input in the east. Note: this could’ve been done more easily with a GridBagLayout, but I wanted to show to flexibility and functionality of nesting your panels. In this way, if I wanted to update only the bottom half of my window, I could simply update that panel with a validate() and repaint().
![Page 2](https://i.imgur.com/7ZLkOJ0.png)

This panel was accomplished through a single JPanel, which contained a loop to spin off these labels and text fields.
![Third Page](https://i.imgur.com/Xv45JwD.png)
