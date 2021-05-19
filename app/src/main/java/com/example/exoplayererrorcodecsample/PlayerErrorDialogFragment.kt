package com.example.exoplayererrorcodecsample

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class PlayerErrorDialogFragment : DialogFragment() {

    private lateinit var errorTitle: String
    private lateinit var errorMessage: String

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireArguments().apply {
            errorTitle = getString(ERROR_TITLE_EXTRA, "")
            errorMessage = getString(ERROR_MESSAGE_EXTRA, "")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(errorTitle)
            .setMessage(errorMessage)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                dismiss()
            }
            .create()
    }

    companion object {
        const val PLAYER_ERROR_DIALOG_FRAGMENT_TAG = "PLAYER_ERROR_DIALOG_FRAGMENT_TAG"

        private const val ERROR_TITLE_EXTRA = "ERROR_TITLE_EXTRA"
        private const val ERROR_MESSAGE_EXTRA = "ERROR_MESSAGE_EXTRA"

        fun newInstance(
            errorTitle: String,
            errorMessage: String
        ) = PlayerErrorDialogFragment().also {
            it.arguments = Bundle().apply {
                putString(ERROR_TITLE_EXTRA, errorTitle)
                putString(ERROR_MESSAGE_EXTRA, errorMessage)
            }
        }
    }
}