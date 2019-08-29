Το προγραμμα ειναι σχεδιασμενο να τρεχει σε λειτουργικο Windows.

*** Τα εικονιδια πρεπει να περιεχονται μεσα στο package σε ενα φακελο icons ( οπως ειναι μεσα στο zip)***

*** Αν ειμασε σε λειτουργικο Linux, τοτε πρεπει να αλλαξουμε την 181 σειρα στο FileBrowser.java << userDir = userDir.substring(1); >> ***

Εντολες μεταγλωτισης :

1) Μεσα απο τον φαεκλο Homework3 γραφουμε την εξης εντολη :
javac src\gr\uth\inf\ce325\file_browser\FileBrowser.java -cp src -d classes  

2) Απο το φακελο classes γραφουμε την εξης εντολη :  
java gr.uth.inf.ce325.file_browser.FileBrowser -cp .
