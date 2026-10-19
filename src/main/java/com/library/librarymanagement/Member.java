
package com.library.librarymanagement;

public class Member {
    private int memberId;
    private String name;
    private String email;
    private String phone;

    public Member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Member(int memberId, String name, String email, String phone) {
        this(name, email, phone);
        this.memberId = memberId;
    }

    public int getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    @Override
    public String toString() {
        return String.format("%-4d %-22s %-28s %s", memberId, name, email, phone);
    }
}