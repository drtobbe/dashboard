package org.sagebionetworks.dashboard.parse;

import java.util.regex.Pattern;

public class UriChangePasswordFilter implements RecordFilter {

    private static final Pattern PATTERN = Pattern.compile(
            "^/auth/v1/userPassword$", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean matches(Record record) {
        String uri = record.getUri();
        if (uri == null) {
            return false; // Otherwise Matcher will throw an NPE
        }
        return PATTERN.matcher(uri).matches();
    }
}
