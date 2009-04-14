/*
 * @author max
 */
package com.intellij.history;

import com.intellij.openapi.vfs.VirtualFile;

public class DeafLocalHistory extends LocalHistory {
  public byte[] getByteContent(final VirtualFile f, final FileRevisionTimestampComparator c) {
    throw new UnsupportedOperationException();
  }

  public boolean hasUnavailableContent(final VirtualFile f) {
    return false;
  }

  public boolean isUnderControl(final VirtualFile f) {
    return false;
  }

  public Checkpoint putCheckpoint() {
    return Checkpoint.NULL_INSTANCE;
  }

  public void putSystemLabel(final String name, final int color) {
  }

  public void putUserLabel(final VirtualFile f, final String name) {
  }

  public void putUserLabel(final String name) {
  }

  public LocalHistoryAction startAction(final String name) {
    return LocalHistoryAction.NULL;
  }

  public void save() {
  }
}
