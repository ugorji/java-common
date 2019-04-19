/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.io;

import java.io.IOException;
import java.io.InputStream;
import net.ugorji.oxygen.util.Closeable;

/**
 * Tries to abstract out a file, whether it comes from a jar, zip or a directory. This way, we can
 * handle things in an abstract way, by leveraging the VirtualFile system.
 *
 * @author ugorji
 */
public interface VirtualFile extends Comparable, Closeable {

  /** All path(s) of VirtualFile are separated by the SEPARATOR_CHAR */
  char SEPARATOR_CHAR = '/';
  /** @return the name (equivalent to the last name in the abstract path sequence */
  String getName();
  /**
   * @return true if this file exists (false otherwise)
   * @throws IOException
   */
  boolean exists() throws IOException;
  /**
   * @return the last modified timestamp as a long
   * @throws IOException
   */
  long lastModified() throws IOException;
  /**
   * @return the size of this VirtualFile
   * @throws IOException
   */
  long size() throws IOException;
  /**
   * @return an InputStream for reading the contents of this VirtualFile
   * @throws IOException
   */
  InputStream getInputStream() throws IOException;
  /**
   * @return the parent of this VirtualFile
   * @throws IOException
   */
  VirtualFile getParent() throws IOException;

  boolean isDirectory() throws IOException;
  /**
   * This can traverse a directory, and return an array listing the VirtualFiles contained within
   * this directory, or sub-directories of. Note, the VirtualFile(s) returned all have to match the
   * VirtualFileFilter. However, it's possible that a parent does not match the vff, but the
   * children do. Consequently, when traversing, put this into account.
   *
   * @param vff: The Filter which is used to trim the results
   * @param maxDepth: the number of sub-directories we can traverse. Only values >= 1 make any sense
   *     for this argument
   */
  VirtualFile[] list(VirtualFileFilter vff, int maxDepth) throws IOException;
  /**
   * @param name the name of the VirtualFile
   * @return a VirtualFile denoted by its name, which is a child of this file
   * @throws IOException
   */
  VirtualFile getChild(String name) throws IOException;
  /** @return the full path of the VirtualFile, separated by SEPARATOR_CHAR */
  String getPath();
}
