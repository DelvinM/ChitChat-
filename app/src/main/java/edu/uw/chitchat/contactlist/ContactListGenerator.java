package edu.uw.chitchat.contactlist;

public final class ContactListGenerator {

    public static final ContactList[] mContacts;
    public static final int COUNT = 20;


    static {
        mContacts = new ContactList[COUNT];
        for (int i = 0; i < mContacts.length; i++) {
            mContacts[i] = new ContactList
                    .Builder("Yohei",
                        "ID 2")
                    .build();
        }
    }


    private ContactListGenerator() { }


}
