/*
 * LunaticSMTP
 * Copyright (C) 2016  Anton Larionov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gitlab.anlar.lunatic.util;

import junit.framework.TestCase;
import org.junit.Test;

public class VersionTest {

    @Test
    public void getVersion() {
        String value = Version.getVersion();
        TestCase.assertNotNull(value);
    }

    @Test
    public void getGitShortRevision() {
        String value = Version.getGitShortRevision();
        TestCase.assertNotNull(value);
    }
}
