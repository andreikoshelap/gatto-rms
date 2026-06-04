'use server';

import { isOidcConfigured, signIn, signOut } from '../auth';
import { isGoogleConfigured } from '../auth';

export async function signInWithOidc() {
  if (!isOidcConfigured) {
    return;
  }

  await signIn('oidc');
}

export async function signInWithGoogle() {
  if (!isGoogleConfigured) {
    return;
  }

  await signIn('google');
}

export async function signOutUser() {
  await signOut({ redirectTo: '/' });
}
