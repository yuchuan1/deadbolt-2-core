/*
 * Copyright 2013 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.objectify.deadbolt.core;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
public class DeadboltAnalyzerTest
{
    @Test
    public void testCheckRole_nullSubject()
    {
        Assert.assertFalse(DeadboltAnalyzer.checkRole(null,
                                                      new String[]{"admin", "editor"}));
    }

    @Test
    public void testCheckRole_nullRoles()
    {
        final Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getRoles()).thenReturn(null);

        Assert.assertFalse(DeadboltAnalyzer.checkRole(subject,
                                                      new String[]{"admin", "editor"}));
    }

    @Test
    public void testCheckRole_emptyRoles()
    {
        final Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getRoles()).thenReturn(Collections.EMPTY_LIST);

        Assert.assertFalse(DeadboltAnalyzer.checkRole(subject,
                                                      new String[]{"admin", "editor"}));
    }

    @Test
    public void testCheckRole_multipleRoles()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertTrue(DeadboltAnalyzer.checkRole(new TestSubject.Builder().roles(roles).build(),
                                                     new String[]{"admin", "editor"}));
    }

    @Test
    public void testCheckRole_withNegation()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertFalse(DeadboltAnalyzer.checkRole(new TestSubject.Builder().roles(roles).build(),
                                                      new String[]{"admin", "!editor"}));
    }

    @Test
    public void testCheckRole_multipleRolesWithNulls()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(null);
        roles.add(new TestRole("editor"));


        Assert.assertTrue(DeadboltAnalyzer.checkRole(new TestSubject.Builder().roles(roles).build(),
                                                     new String[]{"admin", "editor"}));
    }

    @Test
    public void testCheckRole_noMatch()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertFalse(DeadboltAnalyzer.checkRole(new TestSubject.Builder().roles(roles).build(),
                                                      new String[]{"admin", "editor", "foo"}));
    }


    @Test
    public void testCheckRole_noRolesSpecified()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertFalse(DeadboltAnalyzer.checkRole(new TestSubject.Builder().roles(roles).build(),
                                                      new String[0]));
    }

    @Test
    public void testCheckRole_rolesNotPresent()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertFalse(DeadboltAnalyzer.checkRole(new TestSubject.Builder().roles(roles).build(),
                                                      new String[]{"foo", "bar"}));
    }

    @Test
    public void testGetRoleNames_nullSubject()
    {
        final List<String> roleNames = DeadboltAnalyzer.getRoleNames(null);
        Assert.assertNotNull(roleNames);
        Assert.assertTrue(roleNames.isEmpty());
    }

    @Test
    public void testGetRoleNames_nullRoles()
    {
        final Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getRoles()).thenReturn(null);

        final List<String> roleNames = DeadboltAnalyzer.getRoleNames(subject);
        Assert.assertNotNull(roleNames);
        Assert.assertTrue(roleNames.isEmpty());
    }

    @Test
    public void testGetRoleNames_emptyRoles()
    {
        final Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getRoles()).thenReturn(Collections.EMPTY_LIST);

        final List<String> roleNames = DeadboltAnalyzer.getRoleNames(subject);
        Assert.assertNotNull(roleNames);
        Assert.assertTrue(roleNames.isEmpty());
    }

    @Test
    public void testGetRoleNames_multipleRoles()
    {
        List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        final List<String> roleNames = DeadboltAnalyzer.getRoleNames(new TestSubject.Builder().roles(roles)
                                                                             .build());
        Assert.assertNotNull(roleNames);

        Assert.assertEquals(2,
                            roleNames.size());
        Assert.assertTrue(roleNames.contains("admin"));
        Assert.assertTrue(roleNames.contains("editor"));
    }

    @Test
    public void testGetRoleNames_multipleRolesWithNulls()
    {
        List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(null);
        roles.add(new TestRole("editor"));


        final List<String> roleNames = DeadboltAnalyzer.getRoleNames(new TestSubject.Builder().roles(roles)
                                                                             .build());
        Assert.assertNotNull(roleNames);

        Assert.assertEquals(2,
                            roleNames.size());
        Assert.assertTrue(roleNames.contains("admin"));
        Assert.assertTrue(roleNames.contains("editor"));
    }

    @Test
    public void testHasRole_nullSubject()
    {
        Assert.assertFalse(DeadboltAnalyzer.hasRole(null,
                                                    "admin"));
    }

    @Test
    public void testHasRole_nullRoles()
    {
        final Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getRoles()).thenReturn(null);

        Assert.assertFalse(DeadboltAnalyzer.hasRole(subject,
                                                    "admin"));
    }

    @Test
    public void testHasRole_emptyRoles()
    {
        final Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getRoles()).thenReturn(Collections.EMPTY_LIST);

        Assert.assertFalse(DeadboltAnalyzer.hasRole(subject,
                                                    "admin"));
    }

    @Test
    public void testHasRole_multipleRoles()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertTrue(DeadboltAnalyzer.hasRole(new TestSubject.Builder().roles(roles).build(),
                                                   "admin"));
    }

    @Test
    public void testHasRole_multipleRolesWithNulls()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(null);
        roles.add(new TestRole("editor"));


        Assert.assertTrue(DeadboltAnalyzer.hasRole(new TestSubject.Builder().roles(roles).build(),
                                                   "admin"));
    }

    @Test
    public void testHasRole_roleNotPresent()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertFalse(DeadboltAnalyzer.hasRole(new TestSubject.Builder().roles(roles).build(),
                                                    "foo"));
    }

    @Test
    public void testHasAllRoles_nullSubject()
    {
        Assert.assertFalse(DeadboltAnalyzer.hasAllRoles(null,
                                                        new String[]{"admin", "editor"}));
    }

    @Test
    public void testHasAllRoles_nullRoles()
    {
        final Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getRoles()).thenReturn(null);

        Assert.assertFalse(DeadboltAnalyzer.hasAllRoles(subject,
                                                        new String[]{"admin", "editor"}));
    }

    @Test
    public void testHasAllRoles_emptyRoles()
    {
        final Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getRoles()).thenReturn(Collections.EMPTY_LIST);

        Assert.assertFalse(DeadboltAnalyzer.hasAllRoles(subject,
                                                        new String[]{"admin", "editor"}));
    }

    @Test
    public void testHasAllRoles_multipleRoles()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertTrue(DeadboltAnalyzer.hasAllRoles(new TestSubject.Builder().roles(roles).build(),
                                                       new String[]{"admin", "editor"}));
    }

    @Test
    public void testHasAllRoles_multipleRolesWithNulls()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(null);
        roles.add(new TestRole("editor"));


        Assert.assertTrue(DeadboltAnalyzer.hasAllRoles(new TestSubject.Builder().roles(roles).build(),
                                                       new String[]{"admin", "editor"}));
    }

    @Test
    public void testHasAllRoles_noMatch()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertFalse(DeadboltAnalyzer.hasAllRoles(new TestSubject.Builder().roles(roles).build(),
                                                        new String[]{"admin", "editor", "foo"}));
    }


    @Test
    public void testHasAllRoles_noRolesSpecified()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertFalse(DeadboltAnalyzer.hasAllRoles(new TestSubject.Builder().roles(roles).build(),
                                                        new String[0]));
    }

    @Test
    public void testHasAllRoles_rolesNotPresent()
    {
        final List<TestRole> roles = new ArrayList<TestRole>();
        roles.add(new TestRole("admin"));
        roles.add(new TestRole("editor"));


        Assert.assertFalse(DeadboltAnalyzer.hasAllRoles(new TestSubject.Builder().roles(roles).build(),
                                                        new String[]{"foo", "bar"}));
    }

    @Test
    public void testCheckRegexPattern_nullSubject()
    {
        Pattern pattern = Pattern.compile(".*");
        Assert.assertFalse(DeadboltAnalyzer.checkRegexPattern(null,
                                                              pattern));
    }

    @Test
    public void testCheckRegexPattern_nullPattern()
    {
        Assert.assertFalse(DeadboltAnalyzer.checkRegexPattern(new TestSubject.Builder().permissions(Arrays.asList(new TestPermission("printers.edit"))).build(),
                                                              null));
    }

    @Test
    public void testCheckRegexPattern_nullPermissions()
    {
        Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getPermissions()).thenReturn(null);

        Pattern pattern = Pattern.compile(".*");
        Assert.assertFalse(DeadboltAnalyzer.checkRegexPattern(subject,
                                                              pattern));
    }

    @Test
    public void testCheckRegexPattern_emptyPermissions()
    {
        Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getPermissions()).thenReturn(Collections.EMPTY_LIST);

        Pattern pattern = Pattern.compile(".*");
        Assert.assertFalse(DeadboltAnalyzer.checkRegexPattern(subject,
                                                              pattern));
    }

    @Test
    public void testCheckRegexPattern_noMatch()
    {
        Pattern pattern = Pattern.compile(".*(.view)");
        Assert.assertFalse(DeadboltAnalyzer.checkRegexPattern(new TestSubject.Builder().permissions(Arrays.asList(new TestPermission("printers.edit"))).build(),
                                                              pattern));
    }

    @Test
    public void testCheckRegexPattern_match()
    {
        Pattern pattern = Pattern.compile(".*(.edit)");
        Assert.assertTrue(DeadboltAnalyzer.checkRegexPattern(new TestSubject.Builder().permissions(Arrays.asList(new TestPermission("printers.edit"))).build(),
                                                             pattern));
    }

    @Test
    public void testCheckPatternEquality_nullSubject()
    {
        Assert.assertFalse(DeadboltAnalyzer.checkPatternEquality(null,
                                                                 "foo"));
    }

    @Test
    public void testCheckPatternEquality_nullPattern()
    {
        Assert.assertFalse(DeadboltAnalyzer.checkPatternEquality(new TestSubject.Builder().permissions(Arrays.asList(new TestPermission("printers.edit"))).build(),
                                                                 null));
    }

    @Test
    public void testCheckPatternEquality_nullPermissions()
    {
        Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getPermissions()).thenReturn(null);

        Assert.assertFalse(DeadboltAnalyzer.checkPatternEquality(subject,
                                                                 "foo"));
    }

    @Test
    public void testCheckPatternEquality_emptyPermissions()
    {
        Subject subject = Mockito.mock(Subject.class);
        Mockito.when(subject.getPermissions()).thenReturn(Collections.EMPTY_LIST);

        Assert.assertFalse(DeadboltAnalyzer.checkPatternEquality(subject,
                                                                 "foo"));
    }

    @Test
    public void testCheckPatternEquality_noMatch()
    {
        Assert.assertFalse(DeadboltAnalyzer.checkPatternEquality(new TestSubject.Builder().permissions(Arrays.asList(new TestPermission("printers.edit"))).build(),
                                                                 "printers.view"));
    }

    @Test
    public void testCheckPatternEquality_match()
    {
        Assert.assertTrue(DeadboltAnalyzer.checkPatternEquality(new TestSubject.Builder().permissions(Arrays.asList(new TestPermission("printers.edit"))).build(),
                                                                "printers.edit"));
    }

    private static class TestSubject implements Subject
    {
        private String identifier;
        private List<? extends Role> roles;
        private List<? extends Permission> permissions;

        private TestSubject(Builder builder)
        {
            identifier = builder.identifier;
            roles = builder.roles;
            permissions = builder.permissions;
        }

        @Override
        public String getIdentifier()
        {
            return identifier;
        }

        @Override
        public List<? extends Role> getRoles()
        {
            return roles;
        }

        @Override
        public List<? extends Permission> getPermissions()
        {
            return permissions;
        }

        public static final class Builder
        {
            private String identifier;
            private List<? extends Role> roles;
            private List<? extends Permission> permissions;

            public Builder identifier(String identifier)
            {
                this.identifier = identifier;
                return this;
            }

            public Builder roles(List<? extends Role> roles)
            {
                this.roles = roles;
                return this;
            }

            public Builder permissions(List<? extends Permission> permissions)
            {
                this.permissions = permissions;
                return this;
            }

            public TestSubject build()
            {
                return new TestSubject(this);
            }
        }
    }

    private final class TestRole implements Role
    {
        private final String name;

        private TestRole(String name)
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }
    }

    private final class TestPermission implements Permission
    {
        private final String value;

        private TestPermission(String value)
        {
            this.value = value;
        }

        @Override
        public String getValue()
        {
            return value;
        }
    }
}
