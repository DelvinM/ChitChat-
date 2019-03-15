package edu.uw.chitchat.contactlist;

/**
 * the class is generating the contact list
 */
public final class ContactListGenerator {

    /**
     * contactlist's list
     */
    public static final ContactList[] mContacts;
    /**
     * the count number
     */
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

    /**
     * That's constroctor
     */
    private ContactListGenerator() { }


}
