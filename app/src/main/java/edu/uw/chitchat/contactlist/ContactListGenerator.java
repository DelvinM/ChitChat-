package edu.uw.chitchat.contactlist;

public final class ContactListGenerator {

    public static final ContactList[] BLOGS;
    public static final int COUNT = 20;


    static {
        BLOGS = new ContactList[COUNT];
        for (int i = 0; i < BLOGS.length; i++) {
            BLOGS[i] = new ContactList
                    .Builder("Yohei",
                        "ID 2")
                    .build();
        }
    }


    private ContactListGenerator() { }


}
