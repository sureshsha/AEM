document.addEventListener("DOMContentLoaded", function () {
    const signInTab = document.getElementById("signInTab");
    const signUpTab = document.getElementById("signUpTab");
    const signInForm = document.getElementById("signInForm");
    const signUpForm = document.getElementById("signUpForm");
    const messageEl = document.getElementById("googleLoginMessage");

    function setActiveTab(isSignIn) {
        if (isSignIn) {
            signInForm.classList.remove("hidden");
            signUpForm.classList.add("hidden");

            signInTab.classList.add("bg-white", "text-slate-900", "shadow-sm");
            signInTab.classList.remove("text-slate-600");

            signUpTab.classList.remove("bg-white", "text-slate-900", "shadow-sm");
            signUpTab.classList.add("text-slate-600");
        } else {
            signUpForm.classList.remove("hidden");
            signInForm.classList.add("hidden");

            signUpTab.classList.add("bg-white", "text-slate-900", "shadow-sm");
            signUpTab.classList.remove("text-slate-600");

            signInTab.classList.remove("bg-white", "text-slate-900", "shadow-sm");
            signInTab.classList.add("text-slate-600");
        }
    }

    signInTab.addEventListener("click", function () {
        setActiveTab(true);
    });

    signUpTab.addEventListener("click", function () {
        setActiveTab(false);
    });

    function showMessage(msg, isError) {
        if (messageEl) {
            messageEl.textContent = msg;
            messageEl.className = isError
                ? "text-center mt-5 text-sm font-medium text-red-600"
                : "text-center mt-5 text-sm font-medium text-green-600";
        }
    }

    function handleCredentialResponse(response) {
        fetch("/bin/aemtraining/googlelogin", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: "credential=" + encodeURIComponent(response.credential)
        })
        .then(async function (res) {
            const text = await res.text();
            try {
                return JSON.parse(text);
            } catch (e) {
                throw new Error(text);
            }
        })
        .then(function (data) {
            if (data.success) {
                showMessage("Login successful. Redirecting...", false);
                window.location.href = "/content/aemtraining/us/en/thankyou.html";
            } else {
                showMessage(data.message || "Google login failed", true);
            }
        })
        .catch(function (err) {
            console.error(err);
            showMessage("Server error during login", true);
        });
    }

    function initGoogle() {
        if (window.google && google.accounts && google.accounts.id) {
            google.accounts.id.initialize({
                client_id: "948331957795-1a8bt6nedej4jg6co72q5mjfctl55qrl.apps.googleusercontent.com",
                callback: handleCredentialResponse
            });

            google.accounts.id.renderButton(
                document.getElementById("googleSignInBtn"),
                {
                    theme: "outline",
                    size: "large",
                    text: "continue_with",
                    shape: "pill",
                    width: 280
                }
            );
        } else {
            setTimeout(initGoogle, 500);
        }
    }

    initGoogle();
});